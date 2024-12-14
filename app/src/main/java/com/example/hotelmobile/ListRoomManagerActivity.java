package com.example.hotelmobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmobile.adapter.RoomAdapter;
import com.example.hotelmobile.databaseHelper.HotelDBHelper;
import com.example.hotelmobile.databaseHelper.RoomDBHelper;
import com.example.hotelmobile.model.Hotel;
import com.example.hotelmobile.model.Room;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListRoomManagerActivity extends AppCompatActivity {
    private Spinner spinnerHotels;
    private ListView listViewRooms;
    private Button btnAddRoom;
    private HotelDBHelper hotelDBHelper;
    private RoomDBHelper roomDBHelper;
    private List<Hotel> hotelList;
    private List<Room> roomList;
    private RoomAdapter roomAdapter;
    private ArrayAdapter<String> hotelSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_room_manager);

        spinnerHotels = findViewById(R.id.spinnerHotels);
        listViewRooms = findViewById(R.id.listViewRooms);
        btnAddRoom = findViewById(R.id.btnAddRoom);

        hotelDBHelper = new HotelDBHelper();
        roomDBHelper = new RoomDBHelper();
        hotelList = new ArrayList<>();
        roomList = new ArrayList<>();

        // Cấu hình Adapter cho Spinner Hotel
        hotelSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        hotelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHotels.setAdapter(hotelSpinnerAdapter);

        // Cấu hình Adapter cho ListView Room
        roomAdapter = new RoomAdapter(this, roomList);
        listViewRooms.setAdapter(roomAdapter);

        // Tải danh sách khách sạn và cấu hình Spinner
        loadHotels();

        // Xử lý khi chọn khách sạn trong Spinner
        spinnerHotels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Hiển thị tất cả phòng nếu chọn "Tất cả"
                    loadAllRooms();
                } else {
                    // Hiển thị phòng theo khách sạn được chọn
                    Hotel selectedHotel = hotelList.get(position - 1); // Bỏ qua "Tất cả"
                    loadRoomsByHotel(selectedHotel.getHotelId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                roomList.clear();
                roomAdapter.notifyDataSetChanged();
            }
        });

        // Xử lý khi nhấn nút Add New Room
        btnAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(ListRoomManagerActivity.this, AddRoomActivity.class);
            startActivity(intent);
        });
        listViewRooms.setOnItemClickListener((parent, view, position, id) -> {
            Room selectedRoom = roomList.get(position);
            Intent intent = new Intent(ListRoomManagerActivity.this, EditRoomActivity.class);
            // Truyền từng giá trị riêng lẻ
            intent.putExtra("roomId", selectedRoom.getRoomId());
            intent.putExtra("roomNumber", selectedRoom.getRoomNumber());
            intent.putExtra("price", selectedRoom.getPricePerNight());
            intent.putExtra("available", selectedRoom.isAvailable());
            intent.putExtra("hotelId", selectedRoom.getHotel().getHotelId());
            intent.putExtra("categoryId", selectedRoom.getCategory().getId());
            // Chuyển danh sách ảnh thành ArrayList<String>
            intent.putStringArrayListExtra("images", new ArrayList<>(selectedRoom.getImages()));
            startActivity(intent);
        });
    }

    private void loadHotels() {
        hotelDBHelper.getAllHotels(new HotelDBHelper.DataStatus() {
            @Override
            public void onDataLoaded(List<Hotel> hotels) {
                hotelList.clear();
                hotelList.addAll(hotels);
                // Cập nhật dữ liệu cho Spinner
                List<String> hotelNames = new ArrayList<>();
                hotelNames.add("Tất cả"); // Tùy chọn hiển thị tất cả
                for (Hotel hotel : hotels) {
                    hotelNames.add(hotel.getHotelName());
                }
                hotelSpinnerAdapter.clear();
                hotelSpinnerAdapter.addAll(hotelNames);
                hotelSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ListRoomManagerActivity.this, "Error loading hotels: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllRooms() {
        roomDBHelper.getAllRooms(new RoomDBHelper.DataStatus() {
            @Override
            public void onDataLoaded(List<Room> rooms) {
                for (Room room : rooms) {
                    if (room.getHotel() != null) {
                        int hotelId = room.getHotel().getHotelId();
                        // Thực hiện các thao tác với hotelId
                    } else {
                        // Xử lý khi hotel là null (không có thông tin về khách sạn)
                        Log.e("ListRoomManagerActivity", "Room has no associated hotel");
                    }
                }
                roomList.clear();
                roomList.addAll(rooms);
                //roomAdapter.notifyDataSetChanged();
                runOnUiThread(() -> roomAdapter.notifyDataSetChanged());
            }

            @Override
            public void onDataAdded() {

            }

            @Override
            public void onDataUpdated() {

            }

            @Override
            public void onDataDeleted() {

            }

            @Override
            public void onError(String error) {
                Toast.makeText(ListRoomManagerActivity.this, "Error loading rooms: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadRoomsByHotel(int hotelId) {
        roomDBHelper.getAllRooms(new RoomDBHelper.DataStatus() {
            @Override
            public void onDataLoaded(List<Room> rooms) {
                roomList.clear();
                for (Room room : rooms) {
                    if (room.getHotel().getHotelId() == hotelId) {
                        roomList.add(room);
                    }
                }
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDataAdded() {

            }

            @Override
            public void onDataUpdated() {

            }

            @Override
            public void onDataDeleted() {

            }

            @Override
            public void onError(String error) {
                Toast.makeText(ListRoomManagerActivity.this, "Error loading rooms: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
