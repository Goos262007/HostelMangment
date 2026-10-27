package com.hostel.dao;

import com.hostel.config.DatabaseConnection;
import com.hostel.exception.DatabaseException;
import com.hostel.exception.DuplicateEntityException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data Access Object: RoomDAO
 * Manages database operations on hostel rooms and handles capacity consistency.
 */
public class RoomDAO {
    private final DatabaseConnection dbConfig = DatabaseConnection.getInstance();
    private static final List<Room> mockRooms = new CopyOnWriteArrayList<>();
    private static final AtomicInteger mockIdSeq = new AtomicInteger(8);

    static {
        // Initialize default mock rooms mirroring schema.sql
        mockRooms.add(new Room(1, "A-101", "Single", 1, 1, 9500.00, "Occupied", "Deluxe single occupancy with attached washroom"));
        mockRooms.add(new Room(2, "A-102", "Single", 1, 0, 9500.00, "Available", "Single occupancy room with study desk and balcony"));
        mockRooms.add(new Room(3, "B-201", "Double", 2, 2, 7000.00, "Occupied", "Spacious double bed room with central air cooling"));
        mockRooms.add(new Room(4, "B-202", "Double", 2, 1, 7000.00, "Available", "Double sharing room on 2nd floor, facing lawn"));
        mockRooms.add(new Room(5, "B-203", "Double", 2, 0, 7000.00, "Maintenance", "Under repainting and electrical fixture servicing"));
        mockRooms.add(new Room(6, "C-301", "Triple", 3, 2, 5500.00, "Available", "Triple sharing economy room with wardrobes"));
        mockRooms.add(new Room(7, "C-302", "Triple", 3, 3, 5500.00, "Occupied", "Triple sharing room near study lounge"));
        mockRooms.add(new Room(8, "D-401", "Deluxe", 2, 1, 11000.00, "Available", "Premium AC room with mini fridge"));
    }

    public List<Room> getAllRooms() {
        if (dbConfig.isMockMode()) {
            return new ArrayList<>(mockRooms);
        }

        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number ASC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch rooms: " + e.getMessage(), e);
        }
        return rooms;
    }

    public Room getRoomById(int id) throws ResourceNotFoundException {
        if (dbConfig.isMockMode()) {
            for (Room r : mockRooms) {
                if (r.getId() == id) return r;
            }
            throw new ResourceNotFoundException("Room", id);
        }

        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch room by ID: " + e.getMessage(), e);
        }
        throw new ResourceNotFoundException("Room", id);
    }

    public Room addRoom(Room r) throws DuplicateEntityException {
        if (dbConfig.isMockMode()) {
            for (Room existing : mockRooms) {
                if (existing.getRoomNumber().equalsIgnoreCase(r.getRoomNumber())) {
                    throw new DuplicateEntityException("Room Number", r.getRoomNumber());
                }
            }
            r.setId(mockIdSeq.incrementAndGet());
            r.updateCalculatedStatus();
            mockRooms.add(r);
            return r;
        }

        String sql = "INSERT INTO rooms (room_number, room_type, capacity, occupied_beds, monthly_fee, status, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            r.updateCalculatedStatus();
            ps.setString(1, r.getRoomNumber());
            ps.setString(2, r.getRoomType());
            ps.setInt(3, r.getCapacity());
            ps.setInt(4, r.getOccupiedBeds());
            ps.setDouble(5, r.getMonthlyFee());
            ps.setString(6, r.getStatus());
            ps.setString(7, r.getDescription());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setId(keys.getInt(1));
                }
            }
            return r;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DuplicateEntityException("Room Number", r.getRoomNumber());
            }
            throw new DatabaseException("Failed to insert room: " + e.getMessage(), e);
        }
    }

    public boolean updateRoom(Room r) throws ResourceNotFoundException {
        if (dbConfig.isMockMode()) {
            for (int i = 0; i < mockRooms.size(); i++) {
                if (mockRooms.get(i).getId() == r.getId()) {
                    r.updateCalculatedStatus();
                    mockRooms.set(i, r);
                    return true;
                }
            }
            throw new ResourceNotFoundException("Room", r.getId());
        }

        r.updateCalculatedStatus();
        String sql = "UPDATE rooms SET room_type = ?, capacity = ?, occupied_beds = ?, monthly_fee = ?, status = ?, description = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getRoomType());
            ps.setInt(2, r.getCapacity());
            ps.setInt(3, r.getOccupiedBeds());
            ps.setDouble(4, r.getMonthlyFee());
            ps.setString(5, r.getStatus());
            ps.setString(6, r.getDescription());
            ps.setInt(7, r.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update room: " + e.getMessage(), e);
        }
    }

    public boolean deleteRoom(int id) throws ResourceNotFoundException {
        if (dbConfig.isMockMode()) {
            boolean removed = mockRooms.removeIf(r -> r.getId() == id);
            if (!removed) throw new ResourceNotFoundException("Room", id);
            return true;
        }

        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new ResourceNotFoundException("Room", id);
            return true;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete room: " + e.getMessage(), e);
        }
    }

    public void adjustOccupancy(int roomId, int delta) throws ResourceNotFoundException {
        Room r = getRoomById(roomId);
        int newOccupied = Math.max(0, r.getOccupiedBeds() + delta);
        r.setOccupiedBeds(newOccupied);
        r.updateCalculatedStatus();
        updateRoom(r);
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        return new Room(
            rs.getInt("id"),
            rs.getString("room_number"),
            rs.getString("room_type"),
            rs.getInt("capacity"),
            rs.getInt("occupied_beds"),
            rs.getDouble("monthly_fee"),
            rs.getString("status"),
            rs.getString("description")
        );
    }
}
