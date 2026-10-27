package com.hostel.model;

/**
 * Model Class: RoomAllocation
 * Represents room assignment audit records.
 */
public class RoomAllocation {
    private int id;
    private int studentId;
    private String studentName;
    private String studentRollNo;
    private int roomId;
    private String roomNumber;
    private String roomType;
    private String allocationDate;
    private String vacatingDate;
    private String status; // 'Active', 'Vacated'
    private String remarks;

    public RoomAllocation() {
        this.status = "Active";
    }

    public RoomAllocation(int id, int studentId, String studentName, String studentRollNo,
                          int roomId, String roomNumber, String roomType,
                          String allocationDate, String vacatingDate,
                          String status, String remarks) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNo = studentRollNo;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.allocationDate = allocationDate;
        this.vacatingDate = vacatingDate;
        this.status = status;
        this.remarks = remarks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getStudentRollNo() {
        return studentRollNo;
    }

    public void setStudentRollNo(String studentRollNo) {
        this.studentRollNo = studentRollNo;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(String allocationDate) {
        this.allocationDate = allocationDate;
    }

    public String getVacatingDate() {
        return vacatingDate;
    }

    public void setVacatingDate(String vacatingDate) {
        this.vacatingDate = vacatingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
