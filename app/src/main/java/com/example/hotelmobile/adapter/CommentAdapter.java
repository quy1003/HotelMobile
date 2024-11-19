package com.example.hotelmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hotelmobile.R;
import com.example.hotelmobile.model.Comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentAdapter extends android.widget.BaseAdapter {
    private Context context;
    private List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        }

        TextView tvCommentContent = convertView.findViewById(R.id.tvCommentContent);
        TextView tvCommentTimestamp = convertView.findViewById(R.id.tvCommentTimestamp);
        TextView tvUserName = convertView.findViewById(R.id.tvUserName);
        RatingBar ratingBar = convertView.findViewById(R.id.ratingBar);
        LinearLayout layoutSelectedImages = convertView.findViewById(R.id.layoutSelectedImages);

        Comment comment = comments.get(position);

        ratingBar.setRating(comment.getRating());
        tvCommentContent.setText(comment.getContent());
        tvUserName.setText(comment.getUserName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        tvCommentTimestamp.setText(sdf.format(new Date(comment.getTimestamp())));

        // Clear previous images (to prevent duplication when recycling views)
        layoutSelectedImages.removeAllViews();
        // Add images from comment
        List<String> imageUrls = comment.getImages();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                ImageView imageView = new ImageView(context);

                // Set layout parameters for the image
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 200);
                params.setMargins(8, 8, 8, 8);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                // Load image using Glide
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_background) // Add a placeholder drawable
                        .error(R.drawable.ic_launcher_background) // Add an error drawable
                        .into(imageView);

                // Add the imageView to the layout
                layoutSelectedImages.addView(imageView);
            }
        }
        return convertView;
    }
}
