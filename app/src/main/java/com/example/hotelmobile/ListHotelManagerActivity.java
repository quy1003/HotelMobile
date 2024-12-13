    package com.example.hotelmobile;

    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.widget.Button;
    import android.widget.ListView;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.localbroadcastmanager.content.LocalBroadcastManager;

    import com.example.hotelmobile.adapter.HotelAdapter;
    import com.example.hotelmobile.databaseHelper.HotelDBHelper;
    import com.example.hotelmobile.model.Hotel;

    import java.util.ArrayList;
    import java.util.List;


    public class ListHotelManagerActivity extends AppCompatActivity {
        private ListView listViewHotels;
        private HotelDBHelper hotelDBHelper;
        private List<Hotel> hotelList;
        private HotelAdapter hotelAdapter;
        private BroadcastReceiver hotelAddedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Gọi lại danh sách khách sạn khi nhận được broadcast
                loadHotels();
            }
        };
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_list_hotel_manager);

            listViewHotels = findViewById(R.id.listViewHotels);
            hotelDBHelper = new HotelDBHelper();
            hotelList = new ArrayList<>();

            // Gọi getAllHotels và sử dụng callback để nhận dữ liệu
            hotelDBHelper.getAllHotels(new HotelDBHelper.DataStatus() {
                @Override
                public void onDataLoaded(List<Hotel> hotels) {
                    hotelList.clear();
                    hotelList.addAll(hotels);  // Cập nhật danh sách khách sạn
                    hotelAdapter.notifyDataSetChanged();  // Cập nhật giao diện
                }

                @Override
                public void onError(String error) {
                    // Xử lý khi có lỗi
                    Toast.makeText(ListHotelManagerActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });

            SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String userRole = preferences.getString("user_role", "CUSTOMER");
            // Khởi tạo Adapter và gán cho ListView
            hotelAdapter = new HotelAdapter(this, hotelList,userRole);
            listViewHotels.setAdapter(hotelAdapter);


            // Nút thêm khách sạn
            Button btnAddHotel = findViewById(R.id.btnAddHotel);
            btnAddHotel.setOnClickListener(v -> {
                Intent intent = new Intent(ListHotelManagerActivity.this, AddHotelActivity.class);
                startActivity(intent);
            });
        }
        @Override
        protected void onStart() {
            super.onStart();
            // Đăng ký BroadcastReceiver
            LocalBroadcastManager.getInstance(this).registerReceiver(hotelAddedReceiver, new IntentFilter("HOTEL_ADDED"));
            loadHotels(); // Gọi danh sách khách sạn khi Activity bắt đầu
        }

        @Override
        protected void onStop() {
            super.onStop();
            // Hủy đăng ký BroadcastReceiver
            LocalBroadcastManager.getInstance(this).unregisterReceiver(hotelAddedReceiver);
        }

        private void loadHotels() {
            hotelDBHelper.getAllHotels(new HotelDBHelper.DataStatus() {
                @Override
                public void onDataLoaded(List<Hotel> hotels) {
                    hotelList.clear();
                    hotelList.addAll(hotels); // Cập nhật danh sách khách sạn
                    hotelAdapter.notifyDataSetChanged(); // Cập nhật giao diện
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ListHotelManagerActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
