package com.example.hotelmobile.databaseHelper;

import androidx.annotation.NonNull;

import com.example.hotelmobile.model.Hotel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HotelDBHelper {

    private final DatabaseReference hotelDatabase;

    public HotelDBHelper() {
        // Khởi tạo tham chiếu Firebase Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app/");
        hotelDatabase = firebaseDatabase.getReference("hotels");
    }

    public interface DataStatus {
        void onDataLoaded(List<Hotel> hotels); // Thành công khi lấy dữ liệu
        void onError(String error); // Thất bại
    }

    /**
     * Thêm một khách sạn mới vào Firebase Realtime Database.
     *
     * @param hotel Đối tượng Hotel cần thêm.
     */
    public void addHotel(Hotel hotel) {
        // Truy vấn danh sách hiện có để tìm ID lớn nhất
        hotelDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int maxId = 0;

                // Tìm ID lớn nhất trong danh sách hiện tại
                for (DataSnapshot hotelSnapshot : snapshot.getChildren()) {
                    Hotel existingHotel = hotelSnapshot.getValue(Hotel.class);
                    if (existingHotel != null && existingHotel.getHotelId() > maxId) {
                        maxId = existingHotel.getHotelId();
                    }
                }

                // Tạo ID mới lớn hơn ID lớn nhất hiện có
                int newId = maxId + 1;
                hotel.setHotelId(newId);

                // Lưu khách sạn mới vào Firebase
                hotelDatabase.child(String.valueOf(newId)).setValue(hotel)
                        .addOnSuccessListener(aVoid -> {
                            System.out.println("Hotel added successfully to Firebase with ID: " + newId);
                        })
                        .addOnFailureListener(e -> {
                            System.err.println("Failed to add hotel to Firebase: " + e.getMessage());
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Failed to fetch hotels: " + error.getMessage());
            }
        });
    }

    /**
     * Lấy danh sách tất cả khách sạn.
     *
     * @param dataStatus Callback trả về dữ liệu khách sạn hoặc lỗi.
     */
    public void getAllHotels(DataStatus dataStatus) {
        hotelDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Hotel> hotels = new ArrayList<>();
                for (DataSnapshot hotelSnapshot : snapshot.getChildren()) {
                    Hotel hotel = hotelSnapshot.getValue(Hotel.class);
                    if (hotel != null) {
                        hotels.add(hotel);
                    }
                }
                dataStatus.onDataLoaded(hotels); // Trả kết quả qua callback
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dataStatus.onError(error.getMessage()); // Thông báo lỗi qua callback
            }
        });
    }
    /**
     * Cập nhật thông tin khách sạn.
     *
     * @param hotel Đối tượng Hotel cần cập nhật.
     */
    public void updateHotel(Hotel hotel) {
        String hotelId = String.valueOf(hotel.getHotelId());
        hotelDatabase.child(hotelId).setValue(hotel)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Hotel updated successfully.");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Failed to update hotel: " + e.getMessage());
                });
    }

    /**
     * Xóa một khách sạn khỏi Firebase Realtime Database.
     *
     * @param hotelId ID của khách sạn cần xóa.
     */
    public void deleteHotel(int hotelId) {
        hotelDatabase.child(String.valueOf(hotelId)).removeValue()
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Hotel deleted successfully with ID: " + hotelId);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Failed to delete hotel with ID " + hotelId + ": " + e.getMessage());
                });
    }
}
