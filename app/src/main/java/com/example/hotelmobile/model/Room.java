package com.example.hotelmobile.model;

import java.util.List;
public class Room {
    private Integer roomId;
    private int roomNumber;
    private String roomType;
    private double pricePerNight;
    private int hotelID; // Khóa ngoại tới Hotel
    private String availability;
    private List<String> images;

    public Room(Integer roomId, int roomNumber, String roomType, double pricePerNight, String availability, List<String> images,  int hotelID) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.images = images;
        this.roomNumber = roomNumber;
        this.hotelID = hotelID;
        setAvailability(availability);
    }

    public Room(String roomNumber, String roomType, double pricePerNight, String availability, Hotel selectedHotel) {

    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getHotelID() {
        return hotelID;
    }

    public void setHotelID(int hotelID) {
        this.hotelID = hotelID;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        // Kiểm tra tính hợp lệ của availability, chỉ chấp nhận "Available" hoặc "Booked"
        if (availability.equals("Available") || availability.equals("Booked")) {
            this.availability = availability;
        } else {
            throw new IllegalArgumentException("Availability must be 'Available' or 'Booked'");
        }
    }
    // Phương thức để kiểm tra phòng có sẵn không
    public boolean isAvailable() {
        return "Available".equals(availability);
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    // Phương thức để đặt phòng (thay đổi trạng thái sang "Booked")
    public void bookRoom() {
        if (isAvailable()) {
            this.availability = "Booked";
        } else {
            System.out.println("Room is already booked.");
        }
    }

    // Phương thức để hủy đặt phòng (thay đổi trạng thái sang "Available")
    public void cancelBooking() {
        if (!isAvailable()) {
            this.availability = "Available";
        } else {
            System.out.println("Room is already available.");
        }
    }

}
