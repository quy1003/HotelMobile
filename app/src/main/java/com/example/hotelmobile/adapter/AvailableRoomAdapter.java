package com.example.hotelmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hotelmobile.R;
import com.example.hotelmobile.model.Room;

import java.util.List;

public class AvailableRoomAdapter extends BaseAdapter {
    private Context context;
    private List<Room> rooms;
    private OnRoomBookListener bookListener;

    public interface OnRoomBookListener {
        void onRoomBook(Room room);
    }

    public AvailableRoomAdapter(Context context, List<Room> rooms, OnRoomBookListener listener) {
        this.context = context;
        this.rooms = rooms;
        this.bookListener = listener;
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int position) {
        return rooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_available_room, parent, false);
        }

        Room room = rooms.get(position);

        ImageView imageViewRoom = convertView.findViewById(R.id.imageViewRoom);
        TextView textViewRoomNumber = convertView.findViewById(R.id.textViewRoomNumber);
        TextView textViewCategory = convertView.findViewById(R.id.textViewCategory);
        TextView textViewPrice = convertView.findViewById(R.id.textViewPrice);
        Button buttonBookNow = convertView.findViewById(R.id.buttonBookNow);

        textViewRoomNumber.setText("Room " + room.getRoomNumber());
        textViewCategory.setText(room.getCategory().getName());
        textViewPrice.setText(String.format("%,d VND per night", room.getPricePerNight()));

        if (room.getImages() != null && !room.getImages().isEmpty()) {
            Glide.with(context)
                    .load(room.getImages().get(0))
                    .into(imageViewRoom);
        }

        buttonBookNow.setOnClickListener(v -> {
            if (bookListener != null) {
                bookListener.onRoomBook(room);
            }
        });

        return convertView;
    }
}