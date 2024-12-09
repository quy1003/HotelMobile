package com.example.hotelmobile;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class LibraryFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvName;
    private ImageView imgUserAvatar;
    private Button btnEditUserInfo, btnSaveUserInfo;
    private String selectedImagePath = null;

    private Cloudinary cloudinary;

    private static final String PREFERENCE_NAME = "UserPrefs";
    private static final String USER_ID_KEY = "user_id";

    private DatabaseReference userDatabaseReference;
    private int userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Liên kết các thành phần giao diện
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvName = view.findViewById(R.id.tvUserPhone);
        imgUserAvatar = view.findViewById(R.id.imgUserAvatar);
        btnEditUserInfo = view.findViewById(R.id.btnEditUserInfo);
        btnSaveUserInfo = view.findViewById(R.id.btnSaveUserInfo);
        //
        //
        // Khởi tạo Cloudinary
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dbdd85bp4",
                "api_key", "947314781637449",
                "api_secret", "aEQ5nlEGafd_SBz7ZxK2QfcCzWQ"
        ));

        // Lấy user_id từ SharedPreferences
        userId = requireContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(USER_ID_KEY, -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "User ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tải thông tin người dùng từ Firebase
        userDatabaseReference = FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");
        loadUserInfo();

        // Xử lý sự kiện chọn ảnh
        imgUserAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        // Xử lý khi nhấn nút chỉnh sửa thông tin
        btnEditUserInfo.setOnClickListener(v -> {
            tvUserName.setEnabled(true);
            tvUserEmail.setEnabled(true);
            tvName.setEnabled(true);
            btnSaveUserInfo.setVisibility(View.VISIBLE); // Hiển thị nút Save
            btnEditUserInfo.setVisibility(View.GONE); // Ẩn nút Edit
        });
        btnSaveUserInfo.setOnClickListener(v -> {
            String updatedUserName = tvUserName.getText().toString().trim();
            String updatedEmail = tvUserEmail.getText().toString().trim();
            String updatedName = tvName.getText().toString().trim();

            if (updatedUserName.isEmpty() || updatedEmail.isEmpty() || updatedName.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lưu thông tin vào Firebase
            userDatabaseReference.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        // Cập nhật thông tin người dùng
                        userSnapshot.getRef().child("userName").setValue(updatedUserName);
                        userSnapshot.getRef().child("email").setValue(updatedEmail);
                        userSnapshot.getRef().child("name").setValue(updatedName);


                    }

                    Toast.makeText(getContext(), "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                    tvUserName.setEnabled(false);
                    tvUserEmail.setEnabled(false);
                    tvName.setEnabled(false);
                    btnSaveUserInfo.setVisibility(View.GONE);
                    btnEditUserInfo.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadUserInfo() {
        userDatabaseReference.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String name = userSnapshot.child("name").getValue(String.class);
                        String email = userSnapshot.child("email").getValue(String.class);
                        String username = userSnapshot.child("userName").getValue(String.class);
                        String avatarUrl = userSnapshot.child("avatar").getValue(String.class);

                        // Hiển thị thông tin lên giao diện
                        tvUserName.setText(username != null ? username : "No Name");
                        tvUserEmail.setText(email != null ? email : "No Email");
                        tvName.setText(name != null ? name : "No Name");

                        // Tải ảnh đại diện (nếu có)
                        if (avatarUrl != null) {
                            Glide.with(requireContext())
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.ic_launcher_foreground)
                                    .into(imgUserAvatar);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "User not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load user info!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && requestCode == 100 && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImageUri);
                    File tempFile = new File(getContext().getCacheDir(), "temp_image.jpg");
                    FileOutputStream outputStream = new FileOutputStream(tempFile);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }

                    outputStream.close();
                    inputStream.close();

                    selectedImagePath = tempFile.getAbsolutePath();
                    // Tải ảnh lên Cloudinary
                    new UploadImageTask().execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error processing image!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class UploadImageTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
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
                userDatabaseReference.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            userSnapshot.getRef().child("avatar").setValue(imageUrl);
                            Toast.makeText(getContext(), "Avatar updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to update avatar!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Upload failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
