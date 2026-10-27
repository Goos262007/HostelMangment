package com.hostel.model;

/**
 * Abstract Base Class: Person
 * Demonstrates:
 * 1. Abstraction: Defines common attributes and contract without exposing implementation details.
 * 2. Encapsulation: Private member fields with public getters/setters.
 */
public abstract class Person {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String gender;

    // Default Constructor
    public Person() {
    }

    // Parameterized Constructor
    public Person(int id, String fullName, String email, String phone, String gender) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
    }

    // Abstract methods to be overridden by subclasses (Polymorphism & Abstraction)
    public abstract String getRole();
    public abstract String getDisplayInfo();

    // Getters and Setters (Encapsulation)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        this.fullName = fullName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : "";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone.trim() : "";
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender != null ? gender.trim() : "Other";
    }

    @Override
    public String toString() {
        return getRole() + " [id=" + id + ", name=" + fullName + ", email=" + email + "]";
    }
}
