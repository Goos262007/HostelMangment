package com.hostel.model;

/**
 * Concrete Subclass: Admin
 * Demonstrates:
 * 1. Inheritance: Extends Person.
 * 2. Polymorphism: Implements getRole() and getDisplayInfo().
 */
public class Admin extends Person {
    private String username;
    private String password;
    private String designation;

    public Admin() {
        super();
        this.designation = "Hostel Administrator";
    }

    public Admin(int id, String username, String password, String fullName,
                 String email, String phone, String gender, String designation) {
        super(id, fullName, email, phone, gender);
        this.username = username;
        this.password = password;
        this.designation = designation;
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String getDisplayInfo() {
        return "Hostel Administrator: " + getFullName() + " (" + designation + ")";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
