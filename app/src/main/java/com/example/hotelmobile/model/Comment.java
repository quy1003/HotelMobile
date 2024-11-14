package com.example.hotelmobile.model;

public class Comment {
    private int commentId;
    private int hotelId;      // Khóa ngoại tới Hotel
    private int userId;       // Khóa ngoại tới User
    private String content;
    private double rating;
    private String datePosted;

    // Constructor đầy đủ, bao gồm commentId
    public Comment(int commentId, int hotelId, int userId, String content, double rating, String datePosted) {
        this.commentId = commentId;
        this.hotelId = hotelId;
        this.userId = userId;
        this.content = content;
        this.rating = rating;
        this.datePosted = datePosted;
    }

    // Constructor không có commentId, dùng khi tạo mới
    public Comment(int hotelId, int userId, String content, float rating, String datePosted) {
        this.hotelId = hotelId;
        this.userId = userId;
        this.content = content;
        this.rating = rating;
        this.datePosted = datePosted;
    }

    // Getters và Setters
    public int getCommentId() { return commentId; }
    public void setCommentId(int commentId) { this.commentId = commentId; }

    public int getHotelId() { return hotelId; }
    public void setHotelId(int hotelId) { this.hotelId = hotelId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public double getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getDatePosted() { return datePosted; }
    public void setDatePosted(String datePosted) { this.datePosted = datePosted; }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", hotelId=" + hotelId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", rating=" + rating +
                ", datePosted='" + datePosted + '\'' +
                '}';
    }
}
