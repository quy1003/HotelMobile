package com.example.hotelmobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.hotelmobile.adapter.HotelImageAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class EditHotelActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_IMAGE = 101;

    private EditText edtHotelName, edtHotelAddress, edtHotelDescription;
    private ListView lvHotelImages;
    private Button btnAddImage, btnSave;
    private ArrayList<String> imageList = new ArrayList<>();
    private HotelImageAdapter imageAdapter;
    // Khai báo ProgressDialog
    private ProgressDialog progressDialog;

    private String hotelId;
    private DatabaseReference hotelRef;

    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dbdd85bp4",
            "api_key", "947314781637449",
            "api_secret", "aEQ5nlEGafd_SBz7ZxK2QfcCzWQ"
    ));

    private final int PICK_IMAGE_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hotel);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.setCancelable(false);
        // Ánh xạ các View
        edtHotelName = findViewById(R.id.edtHotelName);
        edtHotelAddress = findViewById(R.id.edtHotelAddress);
        edtHotelDescription = findViewById(R.id.edtHotelDescription);
        lvHotelImages = findViewById(R.id.lvHotelImages);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnSave = findViewById(R.id.btnSave);

        // Lấy Intent
        Intent intent = getIntent();
        if (intent != null) {
            hotelId = String.valueOf(intent.getIntExtra("hotel_id", -1));
        }

        // Khởi tạo Firebase
        hotelRef = FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("hotels").child(hotelId);

        // Tải dữ liệu
        loadHotelData();

        // Thiết lập Adapter cho ListView
        imageAdapter = new HotelImageAdapter(this, imageList, hotelId);
        lvHotelImages.setAdapter(imageAdapter);

        // Thêm sự kiện cho ListView
        lvHotelImages.setOnItemClickListener((parent, view, position, id) -> replaceImage(position));

        // Thêm ảnh mới
        btnAddImage.setOnClickListener(v -> selectImageFromGallery());

        // Lưu thay đổi
        btnSave.setOnClickListener(v -> saveHotelData());
    }

    private void loadHotelData() {
        hotelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String hotelName = snapshot.child("hotelName").getValue(String.class);
                    String location = snapshot.child("location").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    for (DataSnapshot imageSnapshot : snapshot.child("images").getChildren()) {
                        imageList.add(imageSnapshot.getValue(String.class));
                    }

                    edtHotelName.setText(hotelName);
                    edtHotelAddress.setText(location);
                    edtHotelDescription.setText(description);
                    imageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditHotelActivity.this, "Lỗi khi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void replaceImage(int position) {
        // Mở thư viện ảnh
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST + position);
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                uploadImageToCloudinary(selectedImageUri);
            }
        }
    }


    private void uploadImageToCloudinary(Uri imageUri) {
        progressDialog.show(); // Hiển thị trạng thái loading

        new Thread(() -> {
            try {
                // Lấy đường dẫn thực tế của ảnh
                String filePath = getRealPath(this, imageUri);

                // Tải lên Cloudinary
                Map uploadResult = cloudinary.uploader().upload(filePath, ObjectUtils.emptyMap());
                String uploadedImageUrl = (String) uploadResult.get("secure_url");

                runOnUiThread(() -> {
                    progressDialog.dismiss(); // Tắt trạng thái loading
                    if (uploadedImageUrl != null) {
                        // Thêm ảnh mới vào mảng images
                        imageList.add(uploadedImageUrl);
                        imageAdapter.notifyDataSetChanged();

                        // Cập nhật Firebase
                        hotelRef.child("images").setValue(imageList)
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Image added successfully!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update images: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void saveHotelData() {
        // Lưu thông tin khách sạn
        hotelRef.child("hotelName").setValue(edtHotelName.getText().toString().trim());
        hotelRef.child("location").setValue(edtHotelAddress.getText().toString().trim());
        hotelRef.child("description").setValue(edtHotelDescription.getText().toString().trim());
        hotelRef.child("images").setValue(imageList)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public static String getRealPath(Context context, Uri uri) {
        String result = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            if (idx != -1) result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
