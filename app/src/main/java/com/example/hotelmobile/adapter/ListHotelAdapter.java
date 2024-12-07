package com.example.hotelmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hotelmobile.R;
import com.example.hotelmobile.model.Hotel;

import java.util.List;

public class ListHotelAdapter extends BaseAdapter {

    private Context context;
    private List<Hotel> hotelList;

    // Constructor nhận vào context và danh sách khách sạn
    public ListHotelAdapter(Context context, List<Hotel> hotelList) {
        this.context = context;
        this.hotelList = hotelList;
    }

    @Override
    public int getCount() {
        return hotelList.size();
    }

    @Override
    public Object getItem(int position) {
        return hotelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;  // Trả về id của item
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Tạo ViewHolder pattern để tăng hiệu suất
        ViewHolder viewHolder;

        if (convertView == null) {
            // Inflate layout item
            convertView = LayoutInflater.from(context).inflate(R.layout.item_hotel, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.hotelImage = convertView.findViewById(R.id.hotel_image);
            viewHolder.hotelName = convertView.findViewById(R.id.hotel_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Lấy khách sạn tại vị trí hiện tại
        Hotel hotel = hotelList.get(position);
        viewHolder.hotelName.setText(hotel.getHotelName());
        // Glide.with(context).load(hotel.getImages().get(0)).into(viewHolder.hotelImage); // Nếu có hình ảnh

        return convertView;
    }

    // Tạo ViewHolder để tái sử dụng view
    static class ViewHolder {
        TextView hotelName;
        ImageView hotelImage;
    }
}
