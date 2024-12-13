package com.example.hotelmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmobile.R;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HotelImageAdapter extends BaseAdapter {
    private Context context;
    private List<String> imageList;
    private String hotelId;
    public HotelImageAdapter(Context context, List<String> imageList, String hotelId) {
        this.context = context;
        this.imageList = imageList;
        this.hotelId = hotelId;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_hotel_image, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imgHotel);
        Button btnRemove = convertView.findViewById(R.id.btnRemoveImage);

        String imageUrl = imageList.get(position);

        // Load hình ảnh
        Picasso.get().load(imageUrl).placeholder(R.drawable.placeholder_image_background).into(imageView);

        // Xoá ảnh
        btnRemove.setOnClickListener(v -> {
            imageList.remove(position);
            notifyDataSetChanged();

            // Cập nhật Firebase
            FirebaseDatabase.getInstance().getReference("hotels")
                    .child(hotelId)
                    .child("images")
                    .setValue(imageList)
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Image removed successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to update images: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        return convertView;
    }
}

