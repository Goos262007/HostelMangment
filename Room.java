package com.hostel.model;

/**
 * Model Class: Room
 * Demonstrates:
 * 1. Encapsulation: Private state with domain logic safeguards.
 * 2. Domain Validation: Enforces capacity constraints.
 */
public class Room {
    private int id;
    private String roomNumber;
    private String roomType; // 'Single', 'Double', 'Triple', 'Deluxe'
    private int capacity;
    private int occupiedBeds;
    private double monthlyFee;
    private String status; // 'Available', 'Occupied', 'Maintenance'
    private String description;

    public Room() {
        this.roomType = "Double";
        this.capacity = 2;
        this.occupiedBeds = 0;
        this.status = "Available";
        this.monthlyFee = 7000.0;
    }

    public Room(int id, String roomNumber, String roomType, int capacity,
                int occupiedBeds, double monthlyFee, String status, String description) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.capacity = capacity;
        this.occupiedBeds = occupiedBeds;
        this.monthlyFee = monthlyFee;
        this.status = status;
        this.description = description;
        updateCalculatedStatus();
    }

    // Domain Logic Methods
    public boolean isFull() {
        return occupiedBeds >= capacity;
    }

    public int getAvailableBeds() {
        return Math.max(0, capacity - occupiedBeds);
    }

    public boolean canAllocate() {
        return !"Maintenance".equalsIgnoreCase(status) && !isFull();
    }

    public void updateCalculatedStatus() {
        if ("Maintenance".equalsIgnoreCase(this.status)) {
            return;
        }
        if (this.occupiedBeds >= this.capacity) {
            this.status = "Occupied";
        } else {
            this.status = "Available";
        }
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber != null ? roomNumber.trim() : "";
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Room capacity must be at least 1.");
        }
        this.capacity = capacity;
        updateCalculatedStatus();
    }

    public int getOccupiedBeds() {
        return occupiedBeds;
    }

    public void setOccupiedBeds(int occupiedBeds) {
        this.occupiedBeds = Math.max(0, occupiedBeds);
        updateCalculatedStatus();
    }

    public double getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(double monthlyFee) {
        this.monthlyFee = Math.max(0.0, monthlyFee);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status.trim() : "Available";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
