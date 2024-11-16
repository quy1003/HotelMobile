package com.example.hotelmobile;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class LibraryFragment extends Fragment {

    // Components
    EditText edtFullName, edtUserName, edtPassword, edtEmail;
    Button btnRegister, btnChooseImage;
    TextView textViewImage;
    String selectedImagePath = null;

    private Cloudinary cloudinary;

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        // Initialize components
        edtFullName = view.findViewById(R.id.edtName);
        edtUserName = view.findViewById(R.id.edtUsername);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtEmail = view.findViewById(R.id.edtEmail);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnChooseImage = view.findViewById(R.id.button_select_file);
        textViewImage = view.findViewById(R.id.text_file_name);

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
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin và chọn ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Upload image to Cloudinary and save user
            new UploadImageTask().execute(new User(0, fullName, userName, password, email, "CUSTOMER", null));
        });

        return view;
    }
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            // If it's a content URI, get file name from the content resolver
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && requestCode == 100) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    textViewImage.setText(getFileNameFromUri(selectedImageUri));

                    try {
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
                        File tempFile = new File(getActivity().getCacheDir(), "temp_image.jpg");
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
                        Toast.makeText(getContext(), "Không thể xử lý tệp!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
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

                Toast.makeText(getContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                // Clear input fields
                edtFullName.setText("");
                edtUserName.setText("");
                edtPassword.setText("");
                edtEmail.setText("");
                textViewImage.setText("");
                selectedImagePath = null;
            } else {
                Toast.makeText(getContext(), "Tải ảnh lên thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
