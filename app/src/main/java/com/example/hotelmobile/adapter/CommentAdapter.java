package com.example.hotelmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        Comment comment = comments.get(position);
        tvCommentContent.setText(comment.getContent());
        tvUserName.setText(comment.getUserName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        tvCommentTimestamp.setText(sdf.format(new Date(comment.getTimestamp())));

        return convertView;
    }
}
