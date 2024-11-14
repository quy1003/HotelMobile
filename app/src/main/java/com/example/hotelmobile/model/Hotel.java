package com.example.hotelmobile.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private int hotelId;
    private String hotelName;
    private String location;
    private String description;
    private List<String> images; // List of image URLs
    private List<Comment> comments; // List of comments for the hotel

    // Constructor for Hotel with images and comments
    public Hotel(int hotelId, String hotelName, String location, String description, List<String> images, List<Comment> comments) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.location = location;
        this.description = description;
        this.images = images;
        this.comments = comments;
    }


    public Hotel(String hotelName, String location, String description, List<String> images) {
        this.hotelName = hotelName;
        this.location = location;
        this.description = description;
        this.images = images;
    }

    public Hotel(int hotelId, String hotelName, String location, String description, List<String> images) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.location = location;
        this.description = description;
        this.images = images;
    }

    // Getters and Setters for each field
    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    // Method to convert JSON string of images to List<String>

    // Method to convert JSON string of comments to List<Comment>
    public static List<Comment> jsonToComments(String json) {
        // Add your JSON parsing code here to convert JSON to List<Comment>
        // Return the List<Comment> parsed from JSON
        return null; // Placeholder for actual parsing logic
    }

    // Method to convert images list to JSON string
    public String imagesToJson() {
        if (images == null) {
            return "[]"; // Trả về chuỗi JSON rỗng nếu images là null
        }

        JSONArray jsonArray = new JSONArray();
        for (String image : images) {
            jsonArray.put(image);
        }
        return jsonArray.toString();
    }

    public static List<String> jsonToImages(String json) {
        List<String> imageList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                imageList.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageList;
    }
    // Method to convert comments list to JSON string
    public String commentsToJson() {
        // Add your code here to convert List<Comment> comments to JSON string
        return null; // Placeholder for actual conversion logic
    }
    public static List<String> imagesFromJson(String json) {
        // Giả sử bạn sử dụng thư viện như Gson để chuyển đổi JSON thành List
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>(){}.getType();
        return gson.fromJson(json, listType);
    }
}
