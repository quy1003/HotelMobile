package com.example.hotelmobile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmobile.Api.CreateOrder;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class BookingActivity extends AppCompatActivity {

    private TextView tvHotelName, tvRoomName, tvStartDate, tvEndDate, tvTotalPrice, tvPaymentStatus;
    private Button btnStartDate, btnEndDate, btnBookRoom;
    private long pricePerNight;
    private String roomName, hotelName;
    private Calendar startDateCalendar, endDateCalendar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private static final String PREFERENCE_NAME = "com.example.hotelmobile.PREFERENCES";
    private static final String HOTEL_NAME_KEY = "hotel_name_key";
    private static final String ROOM_NAME_KEY = "room_name_key";
    private static final String PRICE_KEY = "price_key";
    double totalBill = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Initialize Views
        tvHotelName = findViewById(R.id.tvHotelName);
        tvRoomName = findViewById(R.id.tvRoomName);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);

        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnBookRoom = findViewById(R.id.btnBookRoom);
        //ZaloPay Init
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        //
        // Get room details from SharedPreferences
        getRoomDetailsFromPreferences();

        // Set hotel and room name from SharedPreferences
        tvHotelName.setText("Hotel: " + hotelName);
        tvRoomName.setText("Room: " + roomName);

        // Initialize Calendar instances for start and end dates
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();
        // Thiết lập startDate là ngày mai
        startDateCalendar.add(Calendar.DAY_OF_YEAR, 1);

        // Thiết lập endDate là 2 ngày nữa
        endDateCalendar.add(Calendar.DAY_OF_YEAR, 2);
        // Set default date values
        tvStartDate.setText(dateFormat.format(startDateCalendar.getTime()));
        tvEndDate.setText(dateFormat.format(endDateCalendar.getTime()));

        // Set onClickListener for Start Date button
        btnStartDate.setOnClickListener(v -> showDatePickerDialog(true));

        // Set onClickListener for End Date button
        btnEndDate.setOnClickListener(v -> showDatePickerDialog(false));

        // Set onClickListener for Booking button
        btnBookRoom.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance(); // Lấy ngày hiện tại

            if (startDateCalendar.before(today)) {
                Toast.makeText(BookingActivity.this, "Start Date must not be before today, start date must be tomorrow", Toast.LENGTH_SHORT).show();
            } else if (startDateCalendar.before(endDateCalendar)) {
                // Tích hợp thanh toán
                CreateOrder orderApi = new CreateOrder();
                try {
                    JSONObject data = orderApi.createOrder(String.valueOf(tvTotalPrice.getText().toString()));
                    Log.d("Amount", tvTotalPrice.getText().toString());
                    String code = data.getString("return_code");
                    if (code.equals("1")) {
                        String token = data.getString("zp_trans_token");
                        ZaloPaySDK.getInstance().payOrder(BookingActivity.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                Intent intent = new Intent(BookingActivity.this, PaymentNotification.class);
                                intent.putExtra("result", "successfully!");

                                startActivity(intent);
                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {
                                Intent intent = new Intent(BookingActivity.this, PaymentNotification.class);
                                intent.putExtra("result", "But it was cancelled !");
                                startActivity(intent);
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                Intent intent = new Intent(BookingActivity.this, PaymentNotification.class);
                                intent.putExtra("result", "But it was failed :( !");
                                startActivity(intent);
                            }
                        });
                    }
                    else{
                        Toast.makeText(BookingActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(BookingActivity.this, "End Date must be after Start Date", Toast.LENGTH_SHORT).show();
            }




        });
    }

    // Retrieve room details from SharedPreferences
    private void getRoomDetailsFromPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        hotelName = preferences.getString(HOTEL_NAME_KEY, "Unknown Hotel");
        roomName = preferences.getString(ROOM_NAME_KEY, "Unknown Room");
        pricePerNight = preferences.getLong(PRICE_KEY, 0);
        tvTotalPrice.setText(String.valueOf(pricePerNight));
    }

    // Show date picker dialog for start date or end date
    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = isStartDate ? startDateCalendar : endDateCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                BookingActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String date = dateFormat.format(calendar.getTime());
                    if (isStartDate) {
                        tvStartDate.setText(date);
                    } else {
                        tvEndDate.setText(date);
                    }
                    calculateTotalPrice();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Calculate the total price based on the start date, end date, and price per night
    private void calculateTotalPrice() {
        long startDateInMillis = startDateCalendar.getTimeInMillis();
        long endDateInMillis = endDateCalendar.getTimeInMillis();

        long diffInMillis = endDateInMillis - startDateInMillis;
        long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);  // Convert milliseconds to days

        if (diffInDays <= 0) {
            Toast.makeText(BookingActivity.this, "The duration between start and end date must be positive", Toast.LENGTH_SHORT).show();
        } else {
            long totalPrice = pricePerNight * diffInDays;
            totalBill = totalPrice;
            tvTotalPrice.setText(String.valueOf(totalPrice));
            tvPaymentStatus.setText("Payment Status: Pending");
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

}
