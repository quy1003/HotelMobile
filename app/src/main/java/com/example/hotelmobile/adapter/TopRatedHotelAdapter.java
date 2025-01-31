package com.example.hotelmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotelmobile.HotelDetail;
import com.example.hotelmobile.R;
import com.example.hotelmobile.model.Hotel;


import java.util.ArrayList;
import java.util.List;

public class TopRatedHotelAdapter extends RecyclerView.Adapter<TopRatedHotelAdapter.HotelViewHolder> {
    private List<Hotel> hotels = new ArrayList<>();
    private final Context context;

    // Constructor
    public TopRatedHotelAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_rated_hotel, parent, false);
        return new HotelViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotels.get(position);

        holder.textViewName.setText(hotel.getHotelName());
        holder.textViewLocation.setText(hotel.getLocation());
        holder.ratingBar.setRating(hotel.getAverageRating());
        holder.textViewRatingCount.setText("(" + hotel.getTotalRatings() + " reviews)");

        // Load hotel image using Glide
        if (hotel.getMainImg() != null && !hotel.getMainImg().isEmpty()) {
            Glide.with(holder.imageViewHotel.getContext())
                    .load(hotel.getMainImg())
                    .into(holder.imageViewHotel);
        }

        // Set onClick listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HotelDetail.class);
            intent.putExtra("hotel_id", hotel.getHotelId());
            intent.putExtra("hotel_name", hotel.getHotelName());
            intent.putExtra("hotel_address", hotel.getLocation());
            intent.putExtra("hotel_description", hotel.getDescription());
            intent.putExtra("hotel_image", hotel.getMainImg());
            intent.putExtra("hotel_rating", hotel.getAverageRating());
            intent.putExtra("hotel_reviews", hotel.getTotalRatings());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setHotels(List<Hotel> hotels) {
        this.hotels = hotels;
        notifyDataSetChanged();
    }

    static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewHotel;
        TextView textViewName;
        TextView textViewLocation;
        RatingBar ratingBar;
        TextView textViewRatingCount;

        HotelViewHolder(View itemView) {
            super(itemView);
            imageViewHotel = itemView.findViewById(R.id.imageViewHotel);
            textViewName = itemView.findViewById(R.id.textViewHotelName);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            textViewRatingCount = itemView.findViewById(R.id.textViewRatingCount);
        }
    }
}
