package com.hostel.model;

/**
 * Model Class: Complaint
 * Demonstrates:
 * 1. Encapsulation: Protects student grievance lifecycle.
 * 2. State Progression: Pending -> In Progress -> Resolved.
 */
public class Complaint {
    private int id;
    private String complaintId;
    private int studentId;
    private String studentName;
    private String roomNumber;
    private String title;
    private String description;
    private String category; // 'Plumbing', 'Electrical', 'Housekeeping', 'Wi-Fi', 'Mess', 'Other'
    private String priority; // 'Low', 'Medium', 'High'
    private String status;   // 'Pending', 'In Progress', 'Resolved'
    private String reportedDate;
    private String resolvedDate;
    private String adminRemarks;

    public Complaint() {
        this.priority = "Medium";
        this.status = "Pending";
        this.category = "Other";
    }

    public Complaint(int id, String complaintId, int studentId, String title,
                     String description, String category, String priority,
                     String status, String reportedDate, String resolvedDate,
                     String adminRemarks) {
        this.id = id;
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.reportedDate = reportedDate;
        this.resolvedDate = resolvedDate;
        this.adminRemarks = adminRemarks;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(String reportedDate) {
        this.reportedDate = reportedDate;
    }

    public String getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(String resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }
}
