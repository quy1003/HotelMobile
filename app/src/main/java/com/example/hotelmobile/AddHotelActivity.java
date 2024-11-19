package com.example.hotelmobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.hotelmobile.databaseHelper.HotelDBHelper;
import com.example.hotelmobile.model.Hotel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddHotelActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_IMAGES = 1;
    private EditText edtName, edtLocation, edtDescription;
    private TextView txtFileName;
    private Button btnSelectFile, btnSubmit;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private HotelDBHelper hotelDBHelper;

    // Cloudinary configuration
    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dbdd85bp4",
            "api_key", "947314781637449",
            "api_secret", "aEQ5nlEGafd_SBz7ZxK2QfcCzWQ"
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hotel);

        edtName = findViewById(R.id.edtName);
        edtLocation = findViewById(R.id.edtLocation);
        edtDescription = findViewById(R.id.edtDescription);
        txtFileName = findViewById(R.id.text_file_name);
        btnSelectFile = findViewById(R.id.button_select_file);
        btnSubmit = findViewById(R.id.btnSubmit);

        hotelDBHelper = new HotelDBHelper();

        btnSelectFile.setOnClickListener(view -> selectImages());
        btnSubmit.setOnClickListener(view -> submitHotel());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            WindowInsetsCompat.Type.systemBars();
            return insets;
        });
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

    private void submitHotel() {
        String name = edtName.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(location) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Uploading images, please wait...", Toast.LENGTH_SHORT).show();

        // Upload images to Cloudinary in a new thread
        new Thread(() -> {
            try {
                List<String> imageUrls = new ArrayList<>();

                for (Uri uri : selectedImageUris) {
                    // Use InputStream to read the file
                    try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                        Map uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap());
                        String imageUrl = (String) uploadResult.get("secure_url");
                        imageUrls.add(imageUrl);
                    }
                }

                // Create Hotel object with uploaded image URLs
                Hotel hotel = new Hotel(name, location, description, imageUrls);

                // Add Hotel to Firebase
                hotelDBHelper.addHotel(hotel);

                runOnUiThread(() -> {
                    edtDescription.setText("");
                    edtLocation.setText("");
                    edtName.setText("");
                    Toast.makeText(this, "Hotel added successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent("ListHotelManagerActivity");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    finish();
                });

            } catch (IOException e) {
                Log.e("CloudinaryUpload", "Failed to upload images", e);
                runOnUiThread(() -> Toast.makeText(this, "Failed to upload images", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

}
