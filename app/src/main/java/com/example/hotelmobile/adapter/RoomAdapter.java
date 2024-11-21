package com.example.hotelmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hotelmobile.R;
import com.example.hotelmobile.model.Room;

import java.util.List;

public class RoomAdapter extends BaseAdapter {
    private final Context context;
    private final List<Room> roomList;

    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Object getItem(int position) {
        return roomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
            holder = new ViewHolder();
            holder.txtRoomNumber = convertView.findViewById(R.id.txtRoomNumber);
            holder.txtPrice = convertView.findViewById(R.id.txtPrice);
            holder.txtAvailability = convertView.findViewById(R.id.txtAvailability);
            holder.imgRoom = convertView.findViewById(R.id.imgRoom);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Room room = roomList.get(position);
        holder.txtRoomNumber.setText("Room No: " + room.getRoomNumber());
        holder.txtPrice.setText("Price: $" + room.getPricePerNight());
        holder.txtAvailability.setText("Status: " + (room.isAvailable() ? "Available" : "Booked"));

        // Load the first image (if available)
        if (room.getImages() != null && !room.getImages().isEmpty()) {
            Glide.with(context)
                    .load(room.getImages()) // Load the first image
                    .placeholder(R.drawable.ic_launcher_background) // Placeholder while loading
                    .into(holder.imgRoom);
        } else {
            holder.imgRoom.setImageResource(R.drawable.ic_launcher_background); // Default image
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView txtRoomNumber;
        TextView txtPrice;
        TextView txtAvailability;
        ImageView imgRoom;
    }
}
