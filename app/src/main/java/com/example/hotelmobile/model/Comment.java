package com.example.hotelmobile.model;

public class Comment {
    private String commentId;
    private int hotelId;
    private String content;
    private long timestamp;
    private String userName;
    public Comment() {
    }

    public Comment(String commentId, int hotelId, String content, long timestamp, String userName) {
        this.commentId = commentId;
        this.hotelId = hotelId;
        this.userName = userName;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
