package com.hostel.dao;

import com.hostel.config.DatabaseConnection;
import com.hostel.exception.DatabaseException;
import com.hostel.model.RoomAllocation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data Access Object: AllocationDAO
 * Records historical and active room allotments.
 */
public class AllocationDAO {
    private final DatabaseConnection dbConfig = DatabaseConnection.getInstance();
    private static final List<RoomAllocation> mockAllocations = new CopyOnWriteArrayList<>();
    private static final AtomicInteger mockIdSeq = new AtomicInteger(10);

    static {
        // Initialize mock allocations mirroring seed data
        mockAllocations.add(new RoomAllocation(1, 1, "Aarav Mehta", "21CSE014", 1, "A-101", "Single", "2024-08-01", null, "Active", "Allotted Single Room A-101"));
        mockAllocations.add(new RoomAllocation(2, 2, "Priya Sharma", "22IT045", 3, "B-201", "Double", "2024-08-01", null, "Active", "Allotted Double Room B-201"));
        mockAllocations.add(new RoomAllocation(3, 3, "Rohan Verma", "21ECE028", 3, "B-201", "Double", "2024-08-02", null, "Active", "Allotted Double Room B-201"));
        mockAllocations.add(new RoomAllocation(4, 4, "Ananya Patel", "23CSE089", 4, "B-202", "Double", "2024-08-03", null, "Active", "Allotted Double Room B-202"));
        mockAllocations.add(new RoomAllocation(5, 5, "Vikram Singh", "20ME003", 6, "C-301", "Triple", "2024-08-01", null, "Active", "Allotted Triple Room C-301"));
        mockAllocations.add(new RoomAllocation(6, 6, "Sneha Kulkarni", "22CSE056", 6, "C-301", "Triple", "2024-08-02", null, "Active", "Allotted Triple Room C-301"));
        mockAllocations.add(new RoomAllocation(7, 7, "Kunal Reddy", "21CIV012", 7, "C-302", "Triple", "2024-08-01", null, "Active", "Allotted Triple Room C-302"));
        mockAllocations.add(new RoomAllocation(8, 8, "Tanvi Deshmukh", "23IT019", 7, "C-302", "Triple", "2024-08-01", null, "Active", "Allotted Triple Room C-302"));
        mockAllocations.add(new RoomAllocation(9, 9, "Aditya Nair", "21ECE091", 7, "C-302", "Triple", "2024-08-02", null, "Active", "Allotted Triple Room C-302"));
        mockAllocations.add(new RoomAllocation(10, 10, "Ishaan Gupta", "23CSE110", 8, "D-401", "Deluxe", "2024-08-05", null, "Active", "Allotted Deluxe Room D-401"));
    }

    public List<RoomAllocation> getAllAllocations() {
        if (dbConfig.isMockMode()) {
            return new ArrayList<>(mockAllocations);
        }

        List<RoomAllocation> list = new ArrayList<>();
        String sql = "SELECT a.*, s.full_name, s.roll_number, r.room_number, r.room_type " +
                     "FROM allocations a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN rooms r ON a.room_id = r.id " +
                     "ORDER BY a.id DESC";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RoomAllocation alloc = new RoomAllocation();
                alloc.setId(rs.getInt("id"));
                alloc.setStudentId(rs.getInt("student_id"));
                alloc.setStudentName(rs.getString("full_name"));
                alloc.setStudentRollNo(rs.getString("roll_number"));
                alloc.setRoomId(rs.getInt("room_id"));
                alloc.setRoomNumber(rs.getString("room_number"));
                alloc.setRoomType(rs.getString("room_type"));
                Date alDate = rs.getDate("allocation_date");
                alloc.setAllocationDate(alDate != null ? alDate.toString() : "");
                Date vacDate = rs.getDate("vacating_date");
                alloc.setVacatingDate(vacDate != null ? vacDate.toString() : "");
                alloc.setStatus(rs.getString("status"));
                alloc.setRemarks(rs.getString("remarks"));
                list.add(alloc);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch allocations: " + e.getMessage(), e);
        }
        return list;
    }

    public RoomAllocation createAllocation(RoomAllocation alloc) {
        if (dbConfig.isMockMode()) {
            alloc.setId(mockIdSeq.incrementAndGet());
            if (alloc.getAllocationDate() == null || alloc.getAllocationDate().isEmpty()) {
                alloc.setAllocationDate(java.time.LocalDate.now().toString());
            }
            alloc.setStatus("Active");
            mockAllocations.add(0, alloc);
            return alloc;
        }

        String sql = "INSERT INTO allocations (student_id, room_id, allocation_date, status, remarks) VALUES (?, ?, CURRENT_DATE, 'Active', ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, alloc.getStudentId());
            ps.setInt(2, alloc.getRoomId());
            ps.setString(3, alloc.getRemarks());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    alloc.setId(rs.getInt(1));
                }
            }
            return alloc;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to record allocation: " + e.getMessage(), e);
        }
    }

    public void vacateAllocation(int studentId, int roomId) {
        if (dbConfig.isMockMode()) {
            for (RoomAllocation a : mockAllocations) {
                if (a.getStudentId() == studentId && a.getRoomId() == roomId && "Active".equalsIgnoreCase(a.getStatus())) {
                    a.setStatus("Vacated");
                    a.setVacatingDate(java.time.LocalDate.now().toString());
                    break;
                }
            }
            return;
        }

        String sql = "UPDATE allocations SET status = 'Vacated', vacating_date = CURRENT_DATE WHERE student_id = ? AND room_id = ? AND status = 'Active'";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, roomId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update allocation to vacated: " + e.getMessage(), e);
        }
    }
}
