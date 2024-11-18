package com.example.hotelmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelmobile.HotelDetail;
import com.example.hotelmobile.R;
import com.example.hotelmobile.databaseHelper.HotelDBHelper;
import com.example.hotelmobile.model.Hotel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HotelAdapter extends BaseAdapter {
    private Context context;
    private List<Hotel> hotelList;
    private HotelDBHelper hotelDBHelper;

    public HotelAdapter(Context context, List<Hotel> hotelList) {
        this.context = context;
        this.hotelList = hotelList;
        this.hotelDBHelper = new HotelDBHelper();
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_hotel_manager, parent, false);
        }

        // Kiểm tra nếu hotelList có dữ liệu hợp lệ
        if (hotelList != null && hotelList.size() > 0) {
            Hotel hotel = hotelList.get(position);

            if (hotel != null) {
                TextView tvHotelName = convertView.findViewById(R.id.tvHotelName);
                Button btnEditHotel = convertView.findViewById(R.id.btnEditHotel);
                Button btnDeleteHotel = convertView.findViewById(R.id.btnDeleteHotel);
                ImageView imgMain = convertView.findViewById(R.id.imgViewForItem);
                // Đảm bảo hotel không null trước khi sử dụng
                if (tvHotelName != null) {
                    tvHotelName.setText(hotel.getHotelName());  // Cập nhật tên khách sạn
                }
                if(imgMain != null){
                    Picasso.get()
                            .load(hotel.getMainImg())
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(imgMain);
                }
                // Sự kiện cho nút xoá
                btnDeleteHotel.setOnClickListener(v -> {
                    // Xóa khách sạn khỏi cơ sở dữ liệu và cập nhật danh sách
                    hotelDBHelper.deleteHotel(hotel.getHotelId());
                    hotelList.remove(position);
                    notifyDataSetChanged(); // Làm mới ListView
                    Toast.makeText(context, "Hotel deleted", Toast.LENGTH_SHORT).show();
                });
            }
        }
        // Bắt sự kiện click vào item
        convertView.setOnClickListener(v -> {
            Hotel hotel = hotelList.get(position);
            Intent intent = new Intent(context, HotelDetail.class);
            intent.putExtra("hotel_id", hotel.getHotelId());
            intent.putExtra("hotel_name", hotel.getHotelName());
            intent.putExtra("hotel_address", hotel.getLocation());
            intent.putExtra("hotel_description", hotel.getDescription());
            context.startActivity(intent);
        });
        return convertView;
    }

}
