package com.example.hotelmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RoomDetailActivity extends AppCompatActivity {

    private TextView tvRoomNumber, tvPricePerNight, tvAvailability, tvCategory;
    private ImageView imgRoomImage1, imgRoomImage2, imgRoomImage3;
    private Button btnBookRoom;

    private static final String PREFERENCE_NAME = "com.example.hotelmobile.PREFERENCES";
    private static final String ROOM_ID_KEY = "room_id_key";
    private static final int DEFAULT_ROOM_ID = 1109397610;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        // Initialize views
        tvRoomNumber = findViewById(R.id.tvRoomNumber);
        tvPricePerNight = findViewById(R.id.tvPricePerNight);
        tvCategory = findViewById(R.id.tvCategoryName);
        tvAvailability = findViewById(R.id.tvAvailability);
        imgRoomImage1 = findViewById(R.id.imgRoomImage1);
        imgRoomImage2 = findViewById(R.id.imgRoomImage2);
        imgRoomImage3 = findViewById(R.id.imgRoomImage3);
        btnBookRoom = findViewById(R.id.btnBookRoom);

        // Get room ID from SharedPreferences
        int roomId = getRoomIdFromPreferences();

        // Fetch and display room details
        fetchRoomDetails(roomId);

        // Set onClickListener for booking button
        btnBookRoom.setOnClickListener(v -> {
            Toast.makeText(RoomDetailActivity.this, "Booking functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private int getRoomIdFromPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return preferences.getInt(ROOM_ID_KEY, 1);
    }

    private void fetchRoomDetails(int roomId) {
        DatabaseReference roomRef = FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("rooms");

        roomRef.orderByChild("roomId").equalTo(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                        // Parse room data
                        boolean available = roomSnapshot.child("available").getValue(Boolean.class);
                        int roomNumber = roomSnapshot.child("roomNumber").getValue(Integer.class);

                        long pricePerNight = roomSnapshot.child("pricePerNight").getValue(Long.class);

                        String categoryName = "N/A"; // Default value
                        if (roomSnapshot.child("category").child("name").exists()) {
                            categoryName = roomSnapshot.child("category").child("name").getValue(String.class);
                        }

                        List<String> images = new ArrayList<>();
                        for (DataSnapshot imageSnapshot : roomSnapshot.child("images").getChildren()) {
                            String imageUrl = imageSnapshot.getValue(String.class);
                            if (imageUrl != null) {
                                images.add(imageUrl);
                            }
                        }

                        // Populate UI
                        tvRoomNumber.setText("Room Number: " + roomNumber);
                        tvPricePerNight.setText("Price: " + pricePerNight + " VND/night");
                        tvAvailability.setText(available ? "Availability: Available" : "Availability: Booked");
                        tvAvailability.setTextColor(available
                                ? getResources().getColor(R.color.green)
                                : getResources().getColor(R.color.green));
                        tvCategory.setText("Category: " + categoryName);
                        // Load images into ImageView
                        if (!images.isEmpty()) {
                            Glide.with(RoomDetailActivity.this)
                                    .load(images.get(0))
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .into(imgRoomImage1);

                            if (images.size() > 1) {
                                Glide.with(RoomDetailActivity.this)
                                        .load(images.get(1))
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .into(imgRoomImage2);
                            }

                            if (images.size() > 2) {
                                Glide.with(RoomDetailActivity.this)
                                        .load(images.get(2))
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .into(imgRoomImage3);
                            }
                        }
                    }
                } else {
                    Toast.makeText(RoomDetailActivity.this, "Room not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RoomDetailActivity.this, "Error fetching room details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
