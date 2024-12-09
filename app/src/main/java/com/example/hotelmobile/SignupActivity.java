package com.example.hotelmobile;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.hotelmobile.databaseHelper.UserDBHelper;
import com.example.hotelmobile.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    // Components
    EditText edtFullName, edtUserName, edtPassword, edtEmail;
    Button btnRegister, btnChooseImage;
    TextView textViewImage;
    String selectedImagePath = null;

    private Cloudinary cloudinary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); // Tạo layout cho activity

        // Initialize components
        edtFullName = findViewById(R.id.edtName);
        edtUserName = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        btnRegister = findViewById(R.id.btnRegister);
        btnChooseImage = findViewById(R.id.button_select_file);
        textViewImage = findViewById(R.id.text_file_name);

        // Initialize Cloudinary
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dbdd85bp4",
                "api_key", "947314781637449",
                "api_secret", "aEQ5nlEGafd_SBz7ZxK2QfcCzWQ"
        ));

        // Event: Choose Image
        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        // Event: Register
        btnRegister.setOnClickListener(v -> {
            String fullName = edtFullName.getText().toString();
            String userName = edtUserName.getText().toString();
            String password = edtPassword.getText().toString();
            String email = edtEmail.getText().toString();

            if (fullName.isEmpty() || userName.isEmpty() || password.isEmpty() || email.isEmpty() || selectedImagePath == null) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin và chọn ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Upload image to Cloudinary and save user
            new UploadImageTask().execute(new User(0, fullName, userName, password, email, "CUSTOMER", null));
        });
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            // If it's a content URI, get file name from the content resolver
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (uri.getScheme().equals("file")) {
            // If it's a file URI, get the file name directly
            result = new File(uri.getPath()).getName();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 100) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    textViewImage.setText(getFileNameFromUri(selectedImageUri));

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        File tempFile = new File(getCacheDir(), "temp_image.jpg");
                        FileOutputStream outputStream = new FileOutputStream(tempFile);

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }

                        outputStream.close();
                        inputStream.close();

                        selectedImagePath = tempFile.getAbsolutePath();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Không thể xử lý tệp!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private class UploadImageTask extends AsyncTask<User, Void, String> {
        private User user;

        @Override
        protected String doInBackground(User... users) {
            user = users[0];
            try {
                Log.d("UploadImageTask", "Uploading file: " + selectedImagePath);
                // Upload image to Cloudinary
                Map uploadResult = cloudinary.uploader().upload(selectedImagePath, ObjectUtils.emptyMap());
                return (String) uploadResult.get("secure_url");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String imageUrl) {
            if (imageUrl != null) {
                user.setAvatar(imageUrl);

                // Save user to database
                UserDBHelper dbHelper = new UserDBHelper();
                dbHelper.addUser(user);

                Toast.makeText(SignupActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                finish();
                // Clear input fields
                edtFullName.setText("");
                edtUserName.setText("");
                edtPassword.setText("");
                edtEmail.setText("");
                textViewImage.setText("");
                selectedImagePath = null;
            } else {
                Toast.makeText(SignupActivity.this, "Tải ảnh lên thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}