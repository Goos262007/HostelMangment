package com.hostel.service;

import com.hostel.exception.HostelException;
import com.hostel.model.*;

import java.util.List;
import java.util.Map;

/**
 * Service Contract Interface: HostelOperations
 * Demonstrates:
 * 1. Abstraction: Specifies business functionality contracts without implementation details.
 * 2. Polymorphism: Allows multiple operational implementations (e.g., MySQL Service, In-Memory Service, Mock Service).
 */
public interface HostelOperations {
    // Student Operations
    Student registerStudent(Student student) throws HostelException;
    boolean updateStudent(Student student) throws HostelException;
    boolean removeStudent(int studentId) throws HostelException;
    List<Student> getAllStudents();
    Student getStudentById(int id) throws HostelException;

    // Room Operations
    Room createRoom(Room room) throws HostelException;
    boolean updateRoom(Room room) throws HostelException;
    boolean deleteRoom(int roomId) throws HostelException;
    List<Room> getAllRooms();
    Room getRoomById(int id) throws HostelException;

    // Room Allocation Operations
    RoomAllocation allocateRoom(int studentId, int roomId, String remarks) throws HostelException;
    boolean vacateRoom(int studentId) throws HostelException;
    List<RoomAllocation> getAllAllocations();

    // Fee Management Operations
    Payment recordFeePayment(Payment payment) throws HostelException;
    List<Payment> getAllPayments();

    // Complaint Management Operations
    Complaint registerComplaint(Complaint complaint) throws HostelException;
    boolean updateComplaintStatus(int complaintId, String status, String remarks) throws HostelException;
    List<Complaint> getAllComplaints();

    // Dashboard & Analytics
    Map<String, Object> getDashboardMetrics();
}
