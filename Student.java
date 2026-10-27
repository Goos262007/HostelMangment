package com.hostel.model;

/**
 * Concrete Subclass: Student
 * Demonstrates:
 * 1. Inheritance: Extends abstract base class Person and inherits its attributes.
 * 2. Polymorphism: Overrides getRole() and getDisplayInfo() from Person.
 * 3. Encapsulation: Validated setters and protected internal state.
 */
public class Student extends Person {
    private String studentId;
    private String rollNumber;
    private String department;
    private String yearOfStudy;
    private String dob;
    private String parentContact;
    private String address;
    private Integer assignedRoomId;
    private String assignedRoomNumber;
    private String status; // 'Active', 'Inactive', 'Graduated'
    private String admissionDate;

    public Student() {
        super();
        this.status = "Active";
    }

    public Student(int id, String studentId, String fullName, String rollNumber,
                   String department, String yearOfStudy, String dob, String gender,
                   String phone, String email, String parentContact, String address,
                   Integer assignedRoomId, String status, String admissionDate) {
        super(id, fullName, email, phone, gender);
        this.studentId = studentId;
        this.rollNumber = rollNumber;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.dob = dob;
        this.parentContact = parentContact;
        this.address = address;
        this.assignedRoomId = assignedRoomId;
        this.status = status != null ? status : "Active";
        this.admissionDate = admissionDate;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String getDisplayInfo() {
        return "Student #" + studentId + " - " + getFullName() + " (" + department + " - " + yearOfStudy + ")";
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId != null ? studentId.trim() : "";
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber != null ? rollNumber.trim() : "";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department != null ? department.trim() : "";
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy != null ? yearOfStudy.trim() : "";
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob != null ? dob.trim() : "";
    }

    public String getParentContact() {
        return parentContact;
    }

    public void setParentContact(String parentContact) {
        this.parentContact = parentContact != null ? parentContact.trim() : "";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address != null ? address.trim() : "";
    }

    public Integer getAssignedRoomId() {
        return assignedRoomId;
    }

    public void setAssignedRoomId(Integer assignedRoomId) {
        this.assignedRoomId = assignedRoomId;
    }

    public String getAssignedRoomNumber() {
        return assignedRoomNumber;
    }

    public void setAssignedRoomNumber(String assignedRoomNumber) {
        this.assignedRoomNumber = assignedRoomNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status.trim() : "Active";
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }
}
