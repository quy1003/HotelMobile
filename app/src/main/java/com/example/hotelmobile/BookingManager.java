package com.example.hotelmobile;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelmobile.adapter.BookingManagerAdapter;
import com.example.hotelmobile.model.Booking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingManager extends AppCompatActivity {
    private ListView listViewBookings;
    private BookingManagerAdapter bookingAdapter;
    private List<Booking> bookingList;
    private DatabaseReference bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_manager);

        listViewBookings = findViewById(R.id.listViewBookings);
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingManagerAdapter(this, bookingList);
        listViewBookings.setAdapter(bookingAdapter);

        // Firebase Reference
        bookingsRef = FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("bookings");

        loadBookings();
    }

    private void loadBookings() {
        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear(); // Clear old data
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                    try {
                        String startDateString = bookingSnapshot.child("startDate").getValue(String.class);
                        String endDateString = bookingSnapshot.child("endDate").getValue(String.class);

                        // Chuyển đổi từ String sang Date
                        Date startDate = format.parse(startDateString);
                        Date endDate = format.parse(endDateString);

                        // Ánh xạ các trường còn lại
                        String hotelName = bookingSnapshot.child("hotelName").getValue(String.class);
                        String invoiceCode = bookingSnapshot.child("invoiceCode").getValue(String.class);
                        String paymentStatus = bookingSnapshot.child("paymentStatus").getValue(String.class);
                        String roomName = bookingSnapshot.child("roomName").getValue(String.class);
                        int totalPrice = bookingSnapshot.child("totalPrice").getValue(Integer.class);
                        String userId = bookingSnapshot.child("userId").getValue(String.class);

                        // Tạo đối tượng Booking
                        Booking booking = new Booking();
                        booking.setStartDate(startDate); // Lưu chuỗi để sử dụng lại
                        booking.setEndDate(endDate);
                        booking.setHotelName(hotelName);
                        booking.setInvoiceCode(invoiceCode);
                        booking.setPaymentStatus(paymentStatus);
                        booking.setRoomName(roomName);
                        booking.setTotalPrice(totalPrice);
                        booking.setUserId(userId);

                        bookingList.add(booking);
                    } catch (ParseException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                bookingAdapter.notifyDataSetChanged(); // Update ListView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingManager.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
            }
        });
    }
}