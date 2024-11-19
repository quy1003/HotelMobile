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
import com.example.hotelmobile.databaseHelper.HotelDBHelper;
import com.example.hotelmobile.databaseHelper.RoomDBHelper;
import com.example.hotelmobile.model.Hotel;
import com.example.hotelmobile.model.Room;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddRoomActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    private ImageView imageViewPreview;;
    private EditText edtRoomNumber, edtRoomType, edtPricePerNight;
    private Spinner spinnerAvailability, spinnerHotel;
    private TextView txtFileName;
    private Button btnSelectFile, btnSubmit;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private RoomDBHelper roomDBHelper;
    private HotelDBHelper hotelDBHelper;
    private List<Hotel> hotelList = new ArrayList<>();
    private ArrayAdapter<String> hotelAdapter;

    // Cloudinary configuration
    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dbdd85bp4",
            "api_key", "947314781637449",
            "api_secret", "aEQ5nlEGafd_SBz7ZxK2QfcCzWQ"
    ));
    private final ActivityResultLauncher<String> selectImagesLauncher = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(),
            uris -> {
                if (uris != null && !uris.isEmpty()) {
                    selectedImageUris.clear();
                    selectedImageUris.addAll(uris);

                    // Hiển thị ảnh đầu tiên (nếu có) trong ImageView
                    if (!selectedImageUris.isEmpty()) {
                        imageViewPreview.setImageURI(selectedImageUris.get(0));
                        Toast.makeText(this, selectedImageUris.size() + " images selected", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        edtRoomNumber = findViewById(R.id.edtRoomNumber);
        edtRoomType = findViewById(R.id.edtRoomType);
        edtPricePerNight = findViewById(R.id.edtPricePerNight);
        spinnerAvailability = findViewById(R.id.spinnerAvailability);
        spinnerHotel = findViewById(R.id.spinnerHotel);
        txtFileName = findViewById(R.id.text_file_name);
        btnSelectFile = findViewById(R.id.button_select_file);
        btnSubmit = findViewById(R.id.btnSubmit);
        imageViewPreview = findViewById(R.id.imageViewPreview); // Bạn cần thêm ImageView trong layout


        roomDBHelper = new RoomDBHelper();
        hotelDBHelper = new HotelDBHelper();

        // Cấu hình Spinner Hotel
        hotelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        hotelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHotel.setAdapter(hotelAdapter);

        // Cấu hình Spinner Availability
        ArrayAdapter<String> availabilityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Available", "Booked"});
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAvailability.setAdapter(availabilityAdapter);

        btnSelectFile.setOnClickListener(v -> openGallery());
        btnSubmit.setOnClickListener(view -> submitRoom());

        // Tải danh sách khách sạn
        loadHotels();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            WindowInsetsCompat.Type.systemBars();
            return insets;
        });
    }
    private void openGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK); // Tạo Intent để mở thư viện ảnh
//        intent.setType("image/*"); // Chỉ chọn các tệp hình ảnh
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Cho phép chọn nhiều ảnh
//        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), REQUEST_CODE_SELECT_IMAGE);
        selectImagesLauncher.launch("image/*");
    }


    private void loadHotels() {
        hotelDBHelper.getAllHotels(new HotelDBHelper.DataStatus() {
            @Override
            public void onDataLoaded(List<Hotel> hotels) {
                hotelList.clear();
                hotelList.addAll(hotels);

                // Cập nhật dữ liệu cho Spinner Hotel
                List<String> hotelNames = new ArrayList<>();
                for (Hotel hotel : hotels) {
                    hotelNames.add(hotel.getHotelName());
                }
                hotelAdapter.clear();
                hotelAdapter.addAll(hotelNames);
                hotelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddRoomActivity.this, "Failed to load hotels: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
//            // Nếu người dùng chọn nhiều ảnh
//            if (data.getClipData() != null) {
//                int count = data.getClipData().getItemCount(); // Số lượng ảnh đã chọn
//                for (int i = 0; i < count; i++) {
//                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
//                    // Xử lý từng URI (ví dụ: lưu vào danh sách, hiển thị trong RecyclerView)
//                    selectedImageUris.add(imageUri); // selectedImageUris là danh sách các ảnh đã chọn
//                }
//                Toast.makeText(this, count + " images selected", Toast.LENGTH_SHORT).show();
//            }
//            // Nếu người dùng chỉ chọn một ảnh
//            else if (data.getData() != null) {
//                Uri imageUri = data.getData();
//                selectedImageUris.add(imageUri);
//                Toast.makeText(this, "1 image selected", Toast.LENGTH_SHORT).show();
//            }
//
//            // Hiển thị ảnh đầu tiên trong ImageView (nếu cần)
//            if (!selectedImageUris.isEmpty()) {
//                imageViewPreview.setImageURI(selectedImageUris.get(0)); // Hiển thị ảnh đầu tiên
//            }
//        }
//    }


//    private void selectImages() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), REQUEST_CODE_SELECT_IMAGES);
//    }

    private void submitRoom() {
        String roomNumber = edtRoomNumber.getText().toString().trim();
        String roomType = edtRoomType.getText().toString().trim();
        String pricePerNightStr = edtPricePerNight.getText().toString().trim();
        String availability = spinnerAvailability.getSelectedItem().toString();

        if (TextUtils.isEmpty(roomNumber) || TextUtils.isEmpty(roomType) || TextUtils.isEmpty(pricePerNightStr)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedHotelPosition = spinnerHotel.getSelectedItemPosition();
        if (selectedHotelPosition < 0 || selectedHotelPosition >= hotelList.size()) {
            Toast.makeText(this, "Please select a valid hotel", Toast.LENGTH_SHORT).show();
            return;
        }

        Hotel selectedHotel = hotelList.get(selectedHotelPosition);

        double pricePerNight;
        try {
            pricePerNight = Double.parseDouble(pricePerNightStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Uploading images, please wait...", Toast.LENGTH_SHORT).show();

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

                Room room = new Room(null, Integer.parseInt(roomNumber), roomType, pricePerNight, availability, imageUrls, selectedHotel.getHotelId());

                roomDBHelper.addRoom(room);

                runOnUiThread(() -> {
                    edtRoomNumber.setText("");
                    edtRoomType.setText("");
                    edtPricePerNight.setText("");
                    Toast.makeText(this, "Room added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (IOException e) {
                Log.e("CloudinaryUpload", "Failed to upload images", e);
                runOnUiThread(() -> Toast.makeText(this, "Failed to upload images", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
