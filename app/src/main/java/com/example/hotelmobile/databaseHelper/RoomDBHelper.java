package com.example.hotelmobile.databaseHelper;

import androidx.annotation.NonNull;

import com.example.hotelmobile.model.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RoomDBHelper {

    private final DatabaseReference roomDatabase;

    public RoomDBHelper() {
        // Khởi tạo tham chiếu Firebase Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app/");
        roomDatabase = firebaseDatabase.getReference("rooms");
    }

    public interface DataStatus {
        void onDataLoaded(List<Room> rooms); // Thành công khi lấy dữ liệu
        void onDataAdded(); // Thành công khi thêm dữ liệu
        void onDataUpdated(); // Thành công khi cập nhật dữ liệu
        void onDataDeleted(); // Thành công khi xóa dữ liệu
        void onError(String error); // Lỗi
    }

    /**
     * Thêm một phòng mới vào Firebase Realtime Database.
     *
     * @param room Đối tượng Room cần thêm.
     */
//    public void addRoom(Room room) {
//        // Truy vấn danh sách hiện có để tìm ID lớn nhất
//        roomDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int maxId = 0;
//
//                // Tìm ID lớn nhất trong danh sách hiện tại
//                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
//                    Room existingRoom = roomSnapshot.getValue(Room.class);
//                    if (existingRoom != null && existingRoom.getRoomId() != null && existingRoom.getRoomId() > maxId) {
//                        maxId = existingRoom.getRoomId();
//                    }
//                }
//
//                // Tạo ID mới lớn hơn ID lớn nhất hiện có
//                int newId = maxId + 1;
//                room.setRoomId(newId);
//
//                // Lưu phòng mới vào Firebase
//                roomDatabase.child(String.valueOf(newId)).setValue(room)
//                        .addOnSuccessListener(aVoid -> {
//                            System.out.println("Room added successfully to Firebase with ID: " + newId);
//                        })
//                        .addOnFailureListener(e -> {
//                            System.err.println("Failed to add room to Firebase: " + e.getMessage());
//                        });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                System.err.println("Failed to fetch rooms: " + error.getMessage());
//            }
//        });
//    }
    public void addRoom(Room room, DataStatus status) {
        String roomId = roomDatabase.push().getKey(); // Tạo một ID duy nhất
        if (roomId != null) {
            room.setRoomId(Integer.parseInt(roomId.hashCode() + "")); // Gán ID (hoặc sử dụng trực tiếp)
            roomDatabase.child(roomId).setValue(room)
                    .addOnSuccessListener(aVoid -> status.onDataAdded())
                    .addOnFailureListener(e -> status.onError(e.getMessage()));
        } else {
            status.onError("Failed to generate Room ID");
        }
    }

    /**
     * Lấy danh sách tất cả phòng.
     *
     * @param dataStatus Callback trả về dữ liệu phòng hoặc lỗi.
     */
//    public void getAllRooms(DataStatus dataStatus) {
//        roomDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<Room> rooms = new ArrayList<>();
//                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
//                    Room room = roomSnapshot.getValue(Room.class);
//                    if (room != null) {
//                        rooms.add(room);
//                    }
//                }
//                dataStatus.onDataLoaded(rooms); // Trả kết quả qua callback
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                dataStatus.onError(error.getMessage()); // Thông báo lỗi qua callback
//            }
//        });
//    }
    public void getAllRooms(DataStatus status) {
        roomDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Room> rooms = new ArrayList<>();
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    Room room = roomSnapshot.getValue(Room.class);
                    if (room != null) {
                        rooms.add(room);
                    }
                }
                status.onDataLoaded(rooms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                status.onError(error.getMessage());
            }
        });
    }

    /**
     * Cập nhật thông tin phòng.
     *
     * @param room Đối tượng Room cần cập nhật.
     */
//    public void updateRoom(Room room) {
//        String roomId = String.valueOf(room.getRoomId());
//        roomDatabase.child(roomId).setValue(room)
//                .addOnSuccessListener(aVoid -> {
//                    System.out.println("Room updated successfully.");
//                })
//                .addOnFailureListener(e -> {
//                    System.err.println("Failed to update room: " + e.getMessage());
//                });
//    }
    public void updateRoom(Room room, DataStatus status) {
        String roomId = String.valueOf(room.getRoomId());
        roomDatabase.child(roomId).setValue(room)
                .addOnSuccessListener(aVoid -> status.onDataUpdated())
                .addOnFailureListener(e -> status.onError(e.getMessage()));
    }

    /**
     * Xóa một phòng khỏi Firebase Realtime Database.
     *
     * @param roomId ID của phòng cần xóa.
     */
//    public void deleteRoom(int roomId) {
//        roomDatabase.child(String.valueOf(roomId)).removeValue()
//                .addOnSuccessListener(aVoid -> {
//                    System.out.println("Room deleted successfully with ID: " + roomId);
//                })
//                .addOnFailureListener(e -> {
//                    System.err.println("Failed to delete room with ID " + roomId + ": " + e.getMessage());
//                });
//    }
    public void deleteRoom(int roomId, DataStatus status) {
        roomDatabase.child(String.valueOf(roomId)).removeValue()
                .addOnSuccessListener(aVoid -> status.onDataDeleted())
                .addOnFailureListener(e -> status.onError(e.getMessage()));
    }
}
