package com.example.hotelmobile.model;

import java.util.List;

public class Comment {
    private String id;
    private int hotelId;
    private String content;
    private long timestamp;
    private String userName;
    private float rating;
    private List<String> images; // Thêm danh sách ảnh
    public Comment() {
    }

    public Comment(String id, int hotelId, String content, long timestamp, String userName,float rating, List<String> images) {
        this.id = id;
        this.hotelId = hotelId;
        this.content = content;
        this.timestamp = timestamp;
        this.userName = userName;
        this.images = images;
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
