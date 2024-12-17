package com.example.hotelmobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.hotelmobile.databaseHelper.CategoryDBHelper;
import com.example.hotelmobile.databaseHelper.HotelDBHelper;
import com.example.hotelmobile.databaseHelper.RoomDBHelper;
import com.example.hotelmobile.model.Category;
import com.example.hotelmobile.model.Hotel;
import com.example.hotelmobile.model.Room;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditRoomActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_IMAGES = 1;
    private EditText edtRoomNumber, edtPrice;
    private Spinner spinnerHotel, spinnerCategory, spinnerAvailability;
    private TextView txtFileName;
    private Button btnSelectImages, btnUpdate, btnDelete;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private RoomDBHelper roomDBHelper;
    private HotelDBHelper hotelDBHelper;
    private CategoryDBHelper categoryDBHelper;
    private List<Hotel> hotels = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<String> currentImages;

    // Room data
    private int roomId;
    private int hotelId;
    private int categoryId;

    // Cloudinary config
    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dbdd85bp4",
            "api_key", "947314781637449",
            "api_secret", "aEQ5nlEGafd_SBz7ZxK2QfcCzWQ"
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        // Initialize views and helpers
        initializeViews();

        // Get data from intent
        Intent intent = getIntent();
        roomId = intent.getIntExtra("roomId", -1);
        hotelId = intent.getIntExtra("hotelId", -1);
        categoryId = intent.getIntExtra("categoryId", -1);
        int roomNumber = intent.getIntExtra("roomNumber", 0);
        double price = intent.getDoubleExtra("price", 0.0);
        boolean available = intent.getBooleanExtra("available", true);
        currentImages = intent.getStringArrayListExtra("images");

        if (roomId == -1 || hotelId == -1 || categoryId == -1) {
            Toast.makeText(this, "Error loading room details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set initial data
        edtRoomNumber.setText(String.valueOf(roomNumber));
        edtPrice.setText(String.valueOf(price));
        spinnerAvailability.setSelection(available ? 0 : 1);
        txtFileName.setText((currentImages != null ? currentImages.size() : 0) + " current images");

        // Load data for spinners
        loadHotels();
        loadCategories();
        setupAvailabilitySpinner();
        setupClickListeners();
    }

    private void initializeViews() {
        edtRoomNumber = findViewById(R.id.edtRoomNumber);
        edtPrice = findViewById(R.id.edtPrice);
        spinnerHotel = findViewById(R.id.spinnerHotel);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerAvailability = findViewById(R.id.spinnerAvailability);
        txtFileName = findViewById(R.id.text_file_name);
        btnSelectImages = findViewById(R.id.btnSelectImages);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        roomDBHelper = new RoomDBHelper();
        hotelDBHelper = new HotelDBHelper();
        categoryDBHelper = new CategoryDBHelper();
    }

    private void setupAvailabilitySpinner() {
        ArrayAdapter<String> availabilityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Available", "Booked"});
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAvailability.setAdapter(availabilityAdapter);
    }

    private void loadHotels() {
        hotelDBHelper.getAllHotels(new HotelDBHelper.DataStatus() {
            @Override
            public void onDataLoaded(List<Hotel> hotelList) {
                hotels.clear();
                hotels.addAll(hotelList);

                List<String> hotelNames = new ArrayList<>();
                int selectedIndex = 0;

                for (int i = 0; i < hotels.size(); i++) {
                    Hotel hotel = hotels.get(i);
                    hotelNames.add(hotel.getHotelName());
                    if (hotel.getHotelId() == hotelId) {
                        selectedIndex = i;
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditRoomActivity.this,
                        android.R.layout.simple_spinner_item, hotelNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerHotel.setAdapter(adapter);
                spinnerHotel.setSelection(selectedIndex);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EditRoomActivity.this, "Error loading hotels: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        categoryDBHelper.getAllCategories(new CategoryDBHelper.DataStatus() {
            @Override
            public void onDataLoaded(List<Category> categoryList) {
                categories.clear();
                categories.addAll(categoryList);

                List<String> categoryNames = new ArrayList<>();
                int selectedIndex = 0;

                for (int i = 0; i < categories.size(); i++) {
                    Category category = categories.get(i);
                    categoryNames.add(category.getName());
                    if (category.getId() == categoryId) {
                        selectedIndex = i;
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditRoomActivity.this,
                        android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);
                spinnerCategory.setSelection(selectedIndex);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EditRoomActivity.this, "Error loading categories: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnSelectImages.setOnClickListener(v -> selectImages());
        btnUpdate.setOnClickListener(v -> updateRoom());
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Room")
                    .setMessage("Are you sure you want to delete this room?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteRoom())
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void selectImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), REQUEST_CODE_SELECT_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGES && resultCode == RESULT_OK && data != null) {
            selectedImageUris.clear();

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    selectedImageUris.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                selectedImageUris.add(data.getData());
            }

            txtFileName.setText(selectedImageUris.size() + " new images selected");
        }
    }

    private void updateRoom() {
        String roomNumberStr = edtRoomNumber.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();

        if (TextUtils.isEmpty(roomNumberStr) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Hotel selectedHotel = hotels.get(spinnerHotel.getSelectedItemPosition());
        Category selectedCategory = categories.get(spinnerCategory.getSelectedItemPosition());
        boolean isAvailable = spinnerAvailability.getSelectedItem().toString().equals("Available");

        if (!selectedImageUris.isEmpty()) {
            uploadImagesAndUpdateRoom(roomNumberStr, priceStr, selectedHotel, selectedCategory, isAvailable);
        } else {
            updateRoomInFirebase(
                    Integer.parseInt(roomNumberStr),
                    Double.parseDouble(priceStr),
                    selectedHotel,
                    selectedCategory,
                    isAvailable,
                    currentImages
            );
        }
    }

    private void uploadImagesAndUpdateRoom(String roomNumberStr, String priceStr,
                                           Hotel selectedHotel, Category selectedCategory,
                                           boolean isAvailable) {
        Toast.makeText(this, "Uploading images...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                List<String> imageUrls = new ArrayList<>();
                for (Uri uri : selectedImageUris) {
                    try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                        Map uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap());
                        String imageUrl = (String) uploadResult.get("secure_url");
                        imageUrls.add(imageUrl);
                    }
                }

                runOnUiThread(() -> updateRoomInFirebase(
                        Integer.parseInt(roomNumberStr),
                        Double.parseDouble(priceStr),
                        selectedHotel,
                        selectedCategory,
                        isAvailable,
                        imageUrls
                ));

            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(EditRoomActivity.this,
                        "Error uploading images", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void updateRoomInFirebase(int roomNumber, double price, Hotel hotel,
                                      Category category, boolean isAvailable,
                                      List<String> images) {
        Room updatedRoom = new Room(roomId, roomNumber, price, isAvailable,
                hotel, category, images);

        roomDBHelper.updateRoom(updatedRoom, new RoomDBHelper.DataStatus() {
            @Override
            public void onDataLoaded(List<Room> rooms) {
            }

            @Override
            public void onDataAdded() {
            }

            @Override
            public void onDataUpdated() {
                Toast.makeText(EditRoomActivity.this,
                        "Room updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onDataDeleted() {
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EditRoomActivity.this,
                        "Error updating room: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteRoom() {
        roomDBHelper.deleteRoom(roomId, new RoomDBHelper.DataStatus() {
            @Override
            public void onDataLoaded(List<Room> rooms) {
            }

            @Override
            public void onDataAdded() {
            }

            @Override
            public void onDataUpdated() {
            }

            @Override
            public void onDataDeleted() {
                Toast.makeText(EditRoomActivity.this,
                        "Room deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EditRoomActivity.this,
                        "Error deleting room: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}