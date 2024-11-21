package com.example.hotelmobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class AddRoomActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_IMAGES = 1;
    private EditText edtRoomNumber, edtPrice;
    private Spinner spinnerHotel, spinnerCategory, spinnerAvailability;
    private TextView txtFileName;
    private Button btnSelectImages, btnSubmit;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private RoomDBHelper roomDBHelper;
    private HotelDBHelper hotelDBHelper;
    private CategoryDBHelper categoryDBHelper;
    private List<Hotel> hotels = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    // Cloudinary configuration
    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dbdd85bp4",
            "api_key", "947314781637449",
            "api_secret", "aEQ5nlEGafd_SBz7ZxK2QfcCzWQ"
    ));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        // Initialize Views
        edtRoomNumber = findViewById(R.id.edtRoomNumber);
        edtPrice = findViewById(R.id.edtPrice);
        spinnerHotel = findViewById(R.id.spinnerHotel);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerAvailability = findViewById(R.id.spinnerAvailability);
        txtFileName = findViewById(R.id.text_file_name);
        btnSelectImages = findViewById(R.id.btnSelectImages);
        btnSubmit = findViewById(R.id.btnSubmit);

        roomDBHelper = new RoomDBHelper();
        hotelDBHelper = new HotelDBHelper();
        categoryDBHelper = new CategoryDBHelper();

        // Load Hotels and Categories
        loadHotels();
        loadCategories();

        // Setup Availability Spinner
        ArrayAdapter<String> availabilityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Available", "Booked"});
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAvailability.setAdapter(availabilityAdapter);

        // Select images
        btnSelectImages.setOnClickListener(v -> selectImages());

        // Submit Room
        btnSubmit.setOnClickListener(v -> submitRoom());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGES && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUris.clear();

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    selectedImageUris.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                selectedImageUris.add(data.getData());
            }

            txtFileName.setText(selectedImageUris.size() + " items selected");
        }
    }

    private void selectImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), REQUEST_CODE_SELECT_IMAGES);
    }

    private void loadHotels() {
        hotelDBHelper.getAllHotels(new HotelDBHelper.DataStatus() {
            @Override
            public void onDataLoaded(List<Hotel> hotelList) {
                hotels.clear();
                hotels.addAll(hotelList);

                List<String> hotelNames = new ArrayList<>();
                for (Hotel hotel : hotels) {
                    hotelNames.add(hotel.getHotelName());
                }

                ArrayAdapter<String> hotelAdapter = new ArrayAdapter<>(AddRoomActivity.this,
                        android.R.layout.simple_spinner_item, hotelNames);
                hotelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerHotel.setAdapter(hotelAdapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddRoomActivity.this, "Failed to load hotels: " + error, Toast.LENGTH_SHORT).show();
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
                for (Category category : categories) {
                    categoryNames.add(category.getName());
                }

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(AddRoomActivity.this,
                        android.R.layout.simple_spinner_item, categoryNames);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(categoryAdapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddRoomActivity.this, "Failed to load categories: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitRoom() {
        String roomNumberStr = edtRoomNumber.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String availability = spinnerAvailability.getSelectedItem().toString();
        int selectedHotelPosition = spinnerHotel.getSelectedItemPosition();
        int selectedCategoryPosition = spinnerCategory.getSelectedItemPosition();

        if (TextUtils.isEmpty(roomNumberStr) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedHotelPosition < 0 || selectedCategoryPosition < 0) {
            Toast.makeText(this, "Please select valid Hotel and Category", Toast.LENGTH_SHORT).show();
            return;
        }

        int roomNumber = Integer.parseInt(roomNumberStr);
        double price = Double.parseDouble(priceStr);
        boolean isAvailable = availability.equals("Available");
        Hotel selectedHotel = hotels.get(selectedHotelPosition);
        Category selectedCategory = categories.get(selectedCategoryPosition);

        Toast.makeText(this, "Uploading images, please wait...", Toast.LENGTH_SHORT).show();

        // Upload images to Cloudinary
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

                // Create Room object with uploaded image URLs
                Room room = new Room(0, roomNumber, price, isAvailable, selectedHotel, selectedCategory, imageUrls);

                // Add Room to Firebase
                roomDBHelper.addRoom(room, new RoomDBHelper.DataStatus() {
                    @Override
                    public void onDataLoaded(List<Room> rooms) {

                    }

                    @Override
                    public void onDataAdded() {
                        runOnUiThread(() -> {
                            Toast.makeText(AddRoomActivity.this, "Room added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }

                    @Override
                    public void onDataUpdated() {

                    }

                    @Override
                    public void onDataDeleted() {

                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> Toast.makeText(AddRoomActivity.this, "Failed to add room: " + error, Toast.LENGTH_SHORT).show());
                    }
                });

            } catch (IOException e) {
                Log.e("CloudinaryUpload", "Failed to upload images", e);
                runOnUiThread(() -> Toast.makeText(this, "Failed to upload images", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    public List<Hotel> getHotels() {
        return hotels;
    }

    public void setHotels(List<Hotel> hotels) {
        this.hotels = hotels;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
