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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.txtRoomNumber = convertView.findViewById(R.id.txtRoomNumber);
            viewHolder.txtRoomType = convertView.findViewById(R.id.txtRoomType);
            viewHolder.txtPricePerNight = convertView.findViewById(R.id.txtPricePerNight);
            viewHolder.txtAvailability = convertView.findViewById(R.id.txtAvailability);
            viewHolder.imgRoom = convertView.findViewById(R.id.imgRoom);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Room room = roomList.get(position);

        // Bind data to the views
        viewHolder.txtRoomNumber.setText("Room No: " + room.getRoomNumber());
        viewHolder.txtRoomType.setText("Type: " + room.getRoomType());
        viewHolder.txtPricePerNight.setText("Price: $" + room.getPricePerNight());
        viewHolder.txtAvailability.setText("Status: " + room.getAvailability());

        // Load the first image of the room using Glide (if images are available)
        if (room.getImages() != null && !room.getImages().isEmpty()) {
            Glide.with(context)
                    .load(room.getImages().get(0)) // Load the first image
                    .placeholder(R.drawable.placeholder_image) // Placeholder while loading
                    .into(viewHolder.imgRoom);
        } else {
            viewHolder.imgRoom.setImageResource(R.drawable.placeholder_image); // Default image
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView txtRoomNumber;
        TextView txtRoomType;
        TextView txtPricePerNight;
        TextView txtAvailability;
        ImageView imgRoom;
    }
}
