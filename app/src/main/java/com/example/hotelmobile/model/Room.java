package com.example.hotelmobile.model;

import java.util.List;

public class Room {
    private int roomId;
    private int roomNumber;
    private double pricePerNight;
    private boolean available;
    private Hotel hotel;
    private Category category;
    private List<String> images;

    public Room() {
        this.roomId = 0;
        this.roomNumber = 0;
        this.pricePerNight = 0.0;
        this.available = true; // Mặc định là phòng trống
        this.hotel = null;
        this.category = null;
        this.images = null;
    }

    public Room(int roomId, int roomNumber, double pricePerNight, boolean available,
                Hotel hotel, Category category, List<String> images) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.pricePerNight = pricePerNight;
        this.available = available;
        this.hotel = hotel;
        this.category = category;
        this.images = images;
    }

    // Constructor cơ bản không có hotel và category
    public Room(int roomId, int roomNumber, double pricePerNight, boolean available, List<String> images) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.pricePerNight = pricePerNight;
        this.available = available;
        this.images = images;
        this.hotel = null;
        this.category = null;
    }

    // Constructor cơ bản chỉ có các thông tin chính
    public Room(int roomId, int roomNumber, double pricePerNight) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.pricePerNight = pricePerNight;
        this.available = true; // Mặc định là phòng trống
        this.hotel = null;
        this.category = null;
        this.images = null;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailability(boolean available) {
        this.available = available;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    // Phương thức để đặt phòng (thay đổi trạng thái sang "Booked")
    public void bookRoom() {
        if (isAvailable()) {
            this.available = false;
        } else {
            System.out.println("Room is already booked.");
        }
    }

    // Phương thức để hủy đặt phòng (thay đổi trạng thái sang "Available")
    public void cancelBooking() {
        if (!isAvailable()) {
            this.available = true;
        } else {
            System.out.println("Room is already available.");
        }
    }


}
