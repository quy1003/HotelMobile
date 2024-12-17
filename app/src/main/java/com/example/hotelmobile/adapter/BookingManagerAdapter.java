package com.example.hotelmobile.adapter;

import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hotelmobile.R;
import com.example.hotelmobile.model.Booking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class BookingManagerAdapter extends BaseAdapter {
    private Context context;
    private List<Booking> bookingList;
    private SimpleDateFormat dateFormat;

    public BookingManagerAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return bookingList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_booking_manager, parent, false);
        }

        Booking booking = bookingList.get(position);

        TextView tvHotelName = convertView.findViewById(R.id.tvHotelName);
        TextView tvRoomName = convertView.findViewById(R.id.tvRoomName);
        TextView tvStartDate = convertView.findViewById(R.id.tvStartDate);
        TextView tvEndDate = convertView.findViewById(R.id.tvEndDate);
        TextView tvTotalPrice = convertView.findViewById(R.id.tvTotalPrice);
        TextView tvPaymentStatus = convertView.findViewById(R.id.tvPaymentStatus);

        tvHotelName.setText(booking.getHotelName());
        tvRoomName.setText(booking.getRoomName());
        //
        SimpleDateFormat shortFormat = new SimpleDateFormat("dd/MM/yyyy");
        String shortFormattedDate1 = shortFormat.format(booking.getStartDate());
        String shortFormattedDate2 = shortFormat.format(booking.getEndDate());
        //
        tvStartDate.setText("Start: " + formatDate(shortFormattedDate1));
        tvEndDate.setText("End: " + formatDate(shortFormattedDate2));
        tvTotalPrice.setText("Price: " + booking.getTotalPrice() + " VND");
        tvPaymentStatus.setText("Status: " + booking.getPaymentStatus());

        return convertView;
    }

    private String formatDate(String dateTime) {
        try {
            SimpleDateFormat firebaseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
            Date date = firebaseFormat.parse(dateTime);
            return dateFormat.format(date);
        } catch (Exception e) {
            return dateTime; // Fallback to raw string if parsing fails
        }
    }
}
