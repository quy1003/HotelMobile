package com.example.hotelmobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hotelmobile.adapter.CommentAdapter;
import com.example.hotelmobile.databaseHelper.HotelDBHelper;
import com.example.hotelmobile.model.Hotel;
import com.example.hotelmobile.model.Comment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;
import android.content.Context;

public class HotelDetail extends AppCompatActivity {

    private ImageView imgHotelMain;
    private TextView tvHotelName, tvHotelLocation, tvHotelDescription;
    private LinearLayout layoutHotelImages;
    private ListView listComments;
    private EditText etNewComment;
    private Button btnSubmitComment;

    private List<Comment> comments;
    private CommentAdapter commentAdapter;

    private int hotelId; // ID của khách sạn (truyền từ Intent)
    private Hotel currentHotel; // Thông tin khách sạn hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        // Liên kết các thành phần giao diện
        imgHotelMain = findViewById(R.id.imgHotelMain);
        tvHotelName = findViewById(R.id.tvHotelName);
        tvHotelLocation = findViewById(R.id.tvHotelLocation);
        tvHotelDescription = findViewById(R.id.tvHotelDescription);
        layoutHotelImages = findViewById(R.id.layoutHotelImages);
        listComments = findViewById(R.id.listComments);
        etNewComment = findViewById(R.id.etNewComment);
        btnSubmitComment = findViewById(R.id.btnSubmitComment);

        // Lấy hotelId từ Intent
        hotelId = getIntent().getIntExtra("hotel_id",-1);
        Log.d("Quý check detail", "Hotel ID: " + hotelId);

        if (hotelId == -1) {
            Toast.makeText(this, "Hotel ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tải thông tin khách sạn
        loadHotelDetails();

        // Thiết lập adapter cho bình luận
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, comments);
        listComments.setAdapter(commentAdapter);

        // Tải danh sách bình luận
        loadComments();

        // Xử lý thêm bình luận
        btnSubmitComment.setOnClickListener(v -> submitComment());
    }

    private void loadHotelDetails() {
        FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("hotels")
                .child(String.valueOf(hotelId))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentHotel = snapshot.getValue(Hotel.class);
                        if (currentHotel != null) {
                            // Hiển thị thông tin khách sạn
                            tvHotelName.setText(currentHotel.getHotelName());
                            tvHotelLocation.setText(currentHotel.getLocation());
                            tvHotelDescription.setText(currentHotel.getDescription());

                            // Hiển thị ảnh chính
                            Glide.with(HotelDetail.this)
                                    .load(currentHotel.getMainImg())
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .into(imgHotelMain);

                            // Hiển thị danh sách ảnh
                            if (currentHotel.getImages() != null) {
                                for (String imageUrl : currentHotel.getImages()) {
                                    ImageView imageView = new ImageView(HotelDetail.this);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 200);
                                    params.setMargins(8, 8, 8, 8);
                                    imageView.setLayoutParams(params);
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                    Glide.with(HotelDetail.this)
                                            .load(imageUrl)
                                            .placeholder(R.drawable.ic_launcher_background)
                                            .into(imageView);

                                    layoutHotelImages.addView(imageView);
                                }
                            }
                        } else {
                            Toast.makeText(HotelDetail.this, "Hotel not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HotelDetail.this, "Failed to load hotel details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadComments() {
        FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("comments")
                .child(String.valueOf(hotelId))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        comments.clear();
                        for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                            Comment comment = commentSnapshot.getValue(Comment.class);
                            if (comment != null) {
                                comments.add(comment);
                            }
                        }
                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HotelDetail.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitComment() {
        String commentText = etNewComment.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        String commentId = FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("comments")
                .child(String.valueOf(hotelId))
                .push()
                .getKey();
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", "Nguời dùng");

        Comment newComment = new Comment(commentId, hotelId, commentText, System.currentTimeMillis(), userName);

        FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("comments")
                .child(String.valueOf(hotelId))
                .child(commentId)
                .setValue(newComment)
                .addOnSuccessListener(aVoid -> {
                    // Thông báo thành công và xóa nội dung ô nhập liệu
                    Toast.makeText(this, "Comment added successfully", Toast.LENGTH_SHORT).show();
                    etNewComment.setText("");

                    // Tải lại danh sách bình luận
                    loadComments();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add comment", Toast.LENGTH_SHORT).show());
    }

}
