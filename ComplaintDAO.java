package com.hostel.dao;

import com.hostel.config.DatabaseConnection;
import com.hostel.exception.DatabaseException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.model.Complaint;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data Access Object: ComplaintDAO
 * Handles student grievances, category filters, and resolution statuses.
 */
public class ComplaintDAO {
    private final DatabaseConnection dbConfig = DatabaseConnection.getInstance();
    private static final List<Complaint> mockComplaints = new CopyOnWriteArrayList<>();
    private static final AtomicInteger mockIdSeq = new AtomicInteger(5);

    static {
        // Initialize default mock complaints
        Complaint c1 = new Complaint(1, "CMP-2024-101", 1, "Bathroom tap leaking in A-101", "The washbasin tap has a steady drip.", "Plumbing", "Low", "Resolved", "2024-08-15", "2024-08-16", "Washer replaced by plumber.");
        c1.setStudentName("Aarav Mehta"); c1.setRoomNumber("A-101");
        Complaint c2 = new Complaint(2, "CMP-2024-102", 4, "Wi-Fi router frequent disconnection", "3rd floor wing B router signals drop every 15 minutes.", "Wi-Fi", "High", "In Progress", "2024-08-18", null, "IT team investigating router switch.");
        c2.setStudentName("Ananya Patel"); c2.setRoomNumber("B-202");
        Complaint c3 = new Complaint(3, "CMP-2024-103", 2, "Ceiling fan making clicking noise", "Fan regulator or bearing in B-201 needs lubrication.", "Electrical", "Medium", "Pending", "2024-08-20", null, "Scheduled for electrician visit.");
        c3.setStudentName("Priya Sharma"); c3.setRoomNumber("B-201");
        Complaint c4 = new Complaint(4, "CMP-2024-104", 5, "Daily corridor housekeeping requested", "Dust accumulation on 3rd floor staircase.", "Housekeeping", "Low", "Resolved", "2024-08-17", "2024-08-18", "Cleaning team completed deep scrub.");
        c4.setStudentName("Vikram Singh"); c4.setRoomNumber("C-301");
        Complaint c5 = new Complaint(5, "CMP-2024-105", 7, "Reading lamp socket sparking in C-302", "Power socket near bed 2 sparks when plugging charger.", "Electrical", "High", "Pending", "2024-08-22", null, "Urgent electrician dispatch requested.");
        c5.setStudentName("Kunal Reddy"); c5.setRoomNumber("C-302");

        mockComplaints.add(c1); mockComplaints.add(c2); mockComplaints.add(c3); mockComplaints.add(c4); mockComplaints.add(c5);
    }

    public List<Complaint> getAllComplaints() {
        if (dbConfig.isMockMode()) {
            return new ArrayList<>(mockComplaints);
        }

        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT c.*, s.full_name, r.room_number " +
                     "FROM complaints c " +
                     "JOIN students s ON c.student_id = s.id " +
                     "LEFT JOIN rooms r ON s.assigned_room_id = r.id " +
                     "ORDER BY c.id DESC";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Complaint c = new Complaint();
                c.setId(rs.getInt("id"));
                c.setComplaintId(rs.getString("complaint_id"));
                c.setStudentId(rs.getInt("student_id"));
                c.setStudentName(rs.getString("full_name"));
                c.setRoomNumber(rs.getString("room_number") != null ? rs.getString("room_number") : "Unallocated");
                c.setTitle(rs.getString("title"));
                c.setDescription(rs.getString("description"));
                c.setCategory(rs.getString("category"));
                c.setPriority(rs.getString("priority"));
                c.setStatus(rs.getString("status"));
                Date rep = rs.getDate("reported_date");
                c.setReportedDate(rep != null ? rep.toString() : "");
                Date res = rs.getDate("resolved_date");
                c.setResolvedDate(res != null ? res.toString() : "");
                c.setAdminRemarks(rs.getString("admin_remarks"));
                list.add(c);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch complaints: " + e.getMessage(), e);
        }
        return list;
    }

    public Complaint addComplaint(Complaint c) {
        if (c.getComplaintId() == null || c.getComplaintId().isEmpty()) {
            c.setComplaintId("CMP-" + (System.currentTimeMillis() % 1000000));
        }

        if (dbConfig.isMockMode()) {
            c.setId(mockIdSeq.incrementAndGet());
            if (c.getReportedDate() == null || c.getReportedDate().isEmpty()) {
                c.setReportedDate(java.time.LocalDate.now().toString());
            }
            mockComplaints.add(0, c);
            return c;
        }

        String sql = "INSERT INTO complaints (complaint_id, student_id, title, description, category, priority, status, reported_date, admin_remarks) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_DATE, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getComplaintId());
            ps.setInt(2, c.getStudentId());
            ps.setString(3, c.getTitle());
            ps.setString(4, c.getDescription());
            ps.setString(5, c.getCategory());
            ps.setString(6, c.getPriority());
            ps.setString(7, c.getStatus() != null ? c.getStatus() : "Pending");
            ps.setString(8, c.getAdminRemarks());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                }
            }
            return c;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to register complaint: " + e.getMessage(), e);
        }
    }

    public boolean updateStatus(int id, String status, String remarks) throws ResourceNotFoundException {
        if (dbConfig.isMockMode()) {
            for (Complaint c : mockComplaints) {
                if (c.getId() == id) {
                    c.setStatus(status);
                    c.setAdminRemarks(remarks);
                    if ("Resolved".equalsIgnoreCase(status)) {
                        c.setResolvedDate(java.time.LocalDate.now().toString());
                    }
                    return true;
                }
            }
            throw new ResourceNotFoundException("Complaint", id);
        }

        String sql = "UPDATE complaints SET status = ?, admin_remarks = ?, resolved_date = " +
                     ("Resolved".equalsIgnoreCase(status) ? "CURRENT_DATE" : "NULL") +
                     " WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, remarks);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update complaint status: " + e.getMessage(), e);
        }
    }
}
