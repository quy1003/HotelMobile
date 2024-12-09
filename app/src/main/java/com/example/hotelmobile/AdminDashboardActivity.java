package com.example.hotelmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminDashboardActivity extends AppCompatActivity {
    Button btnCategory, btnRoom, btnBooking, btnHotel, btnOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);

        btnCategory = findViewById(R.id.btnManageCategories);
        btnHotel = findViewById(R.id.btnManageHotels);
        btnRoom = findViewById(R.id.btnManageRooms);
        btnBooking = findViewById(R.id.btnManageBookings);
        btnOut = findViewById(R.id.btnOut);

        btnOut.setOnClickListener(v->{
            startActivity(new Intent(AdminDashboardActivity.this, MainActivity.class));
            finish();
        });
        btnCategory.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, CategoryManagerActivity.class));
            finish();

        });
        btnHotel.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ListHotelManagerActivity.class));
            finish();
        });

        btnRoom.setOnClickListener(V->{
            startActivity(new Intent(AdminDashboardActivity.this, ListRoomManagerActivity.class));
            finish();
        });

        btnBooking.setOnClickListener(v->{
//            startActivity(new Intent(AdminDashboardActivity.this, CategoryManagerActivity.class));
//            finish();
        });
    }
}