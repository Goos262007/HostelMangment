package com.hostel.dao;

import com.hostel.config.DatabaseConnection;
import com.hostel.exception.DatabaseException;
import com.hostel.exception.DuplicateEntityException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data Access Object: StudentDAO
 * Implements JDBC data persistence for students with prepared statements,
 * transaction safety, and fallback mock support.
 */
public class StudentDAO {
    private final DatabaseConnection dbConfig = DatabaseConnection.getInstance();
    private static final List<Student> mockStudents = new CopyOnWriteArrayList<>();
    private static final AtomicInteger mockIdSeq = new AtomicInteger(12);

    static {
        // Initialize default mock records mirroring schema.sql
        mockStudents.add(new Student(1, "STU-2024-001", "Aarav Mehta", "21CSE014", "Computer Science", "3rd Year", "2003-05-12", "Male", "+91 98765 43210", "aarav.mehta@college.edu", "+91 98765 00001", "Flat 402, Green Valley Apts, Pune", 1, "Active", "2024-08-01"));
        mockStudents.add(new Student(2, "STU-2024-002", "Priya Sharma", "22IT045", "Information Technology", "2nd Year", "2004-08-22", "Female", "+91 98765 43211", "priya.sharma@college.edu", "+91 98765 00002", "12B, Rose Villa, Jaipur", 3, "Active", "2024-08-01"));
        mockStudents.add(new Student(3, "STU-2024-003", "Rohan Verma", "21ECE028", "Electronics & Comm", "3rd Year", "2003-11-05", "Male", "+91 98765 43212", "rohan.verma@college.edu", "+91 98765 00003", "78 Sector 14, Chandigarh", 3, "Active", "2024-08-02"));
        mockStudents.add(new Student(4, "STU-2024-004", "Ananya Patel", "23CSE089", "Computer Science", "1st Year", "2005-02-18", "Female", "+91 98765 43213", "ananya.patel@college.edu", "+91 98765 00004", "Plot 55, Royal Residency, Ahmedabad", 4, "Active", "2024-08-03"));
        mockStudents.add(new Student(5, "STU-2024-005", "Vikram Singh", "20ME003", "Mechanical", "4th Year", "2002-09-30", "Male", "+91 98765 43214", "vikram.singh@college.edu", "+91 98765 00005", "House 14, Lake View Road, Bhopal", 6, "Active", "2024-08-01"));
        mockStudents.add(new Student(6, "STU-2024-006", "Sneha Kulkarni", "22CSE056", "Computer Science", "2nd Year", "2004-04-14", "Female", "+91 98765 43215", "sneha.kulkarni@college.edu", "+91 98765 00006", "45 Shivajinagar, Kolhapur", 6, "Active", "2024-08-02"));
        mockStudents.add(new Student(7, "STU-2024-007", "Kunal Reddy", "21CIV012", "Civil", "3rd Year", "2003-07-25", "Male", "+91 98765 43216", "kunal.reddy@college.edu", "+91 98765 00007", "Banjara Hills, Hyderabad", 7, "Active", "2024-08-01"));
        mockStudents.add(new Student(8, "STU-2024-008", "Tanvi Deshmukh", "23IT019", "Information Technology", "1st Year", "2005-10-09", "Female", "+91 98765 43217", "tanvi.d@college.edu", "+91 98765 00008", "Sunrise Towers, Nagpur", 7, "Active", "2024-08-01"));
        mockStudents.add(new Student(9, "STU-2024-009", "Aditya Nair", "21ECE091", "Electronics & Comm", "3rd Year", "2003-01-15", "Male", "+91 98765 43218", "aditya.nair@college.edu", "+91 98765 00009", "Palace Road, Kowdiar, Trivandrum", 7, "Active", "2024-08-02"));
        mockStudents.add(new Student(10, "STU-2024-010", "Ishaan Gupta", "23CSE110", "Computer Science", "1st Year", "2005-06-20", "Male", "+91 98765 43219", "ishaan.gupta@college.edu", "+91 98765 00010", "Model Town, New Delhi", 8, "Active", "2024-08-05"));
        mockStudents.add(new Student(11, "STU-2024-011", "Neha Joshi", "22ECE034", "Electronics & Comm", "2nd Year", "2004-12-03", "Female", "+91 98765 43220", "neha.joshi@college.edu", "+91 98765 00011", "Subhash Marg, Indore", null, "Active", "2024-08-10"));
        mockStudents.add(new Student(12, "STU-2024-012", "Arjun Saxena", "20CSE005", "Computer Science", "4th Year", "2002-03-11", "Male", "+91 98765 43221", "arjun.saxena@college.edu", "+91 98765 00012", "Gomti Nagar, Lucknow", null, "Active", "2024-08-10"));
    }

    public List<Student> getAllStudents() {
        if (dbConfig.isMockMode()) {
            return new ArrayList<>(mockStudents);
        }

        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, r.room_number FROM students s LEFT JOIN rooms r ON s.assigned_room_id = r.id ORDER BY s.id DESC";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch students from MySQL: " + e.getMessage(), e);
        }
        return students;
    }

    public Student getStudentById(int id) throws ResourceNotFoundException {
        if (dbConfig.isMockMode()) {
            for (Student s : mockStudents) {
                if (s.getId() == id) return s;
            }
            throw new ResourceNotFoundException("Student", id);
        }

        String sql = "SELECT s.*, r.room_number FROM students s LEFT JOIN rooms r ON s.assigned_room_id = r.id WHERE s.id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch student by ID: " + e.getMessage(), e);
        }
        throw new ResourceNotFoundException("Student", id);
    }

    public Student addStudent(Student s) throws DuplicateEntityException {
        if (dbConfig.isMockMode()) {
            for (Student existing : mockStudents) {
                if (existing.getStudentId().equalsIgnoreCase(s.getStudentId())) {
                    throw new DuplicateEntityException("Student ID", s.getStudentId());
                }
                if (existing.getRollNumber().equalsIgnoreCase(s.getRollNumber())) {
                    throw new DuplicateEntityException("Roll Number", s.getRollNumber());
                }
            }
            s.setId(mockIdSeq.incrementAndGet());
            mockStudents.add(s);
            return s;
        }

        String sql = "INSERT INTO students (student_id, full_name, roll_number, department, year_of_study, dob, gender, phone, email, parent_contact, address, assigned_room_id, status, admission_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_DATE)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getRollNumber());
            ps.setString(4, s.getDepartment());
            ps.setString(5, s.getYearOfStudy());
            ps.setDate(6, Date.valueOf(s.getDob().isEmpty() ? "2004-01-01" : s.getDob()));
            ps.setString(7, s.getGender());
            ps.setString(8, s.getPhone());
            ps.setString(9, s.getEmail());
            ps.setString(10, s.getParentContact());
            ps.setString(11, s.getAddress());
            if (s.getAssignedRoomId() != null && s.getAssignedRoomId() > 0) {
                ps.setInt(12, s.getAssignedRoomId());
            } else {
                ps.setNull(12, Types.INTEGER);
            }
            ps.setString(13, s.getStatus());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    s.setId(keys.getInt(1));
                }
            }
            return s;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DuplicateEntityException("Student ID or Roll Number", s.getStudentId());
            }
            throw new DatabaseException("Failed to insert student into MySQL: " + e.getMessage(), e);
        }
    }

    public boolean updateStudent(Student s) throws ResourceNotFoundException {
        if (dbConfig.isMockMode()) {
            for (int i = 0; i < mockStudents.size(); i++) {
                if (mockStudents.get(i).getId() == s.getId()) {
                    mockStudents.set(i, s);
                    return true;
                }
            }
            throw new ResourceNotFoundException("Student", s.getId());
        }

        String sql = "UPDATE students SET full_name = ?, roll_number = ?, department = ?, year_of_study = ?, " +
                     "gender = ?, phone = ?, email = ?, parent_contact = ?, address = ?, status = ? " +
                     "WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getFullName());
            ps.setString(2, s.getRollNumber());
            ps.setString(3, s.getDepartment());
            ps.setString(4, s.getYearOfStudy());
            ps.setString(5, s.getGender());
            ps.setString(6, s.getPhone());
            ps.setString(7, s.getEmail());
            ps.setString(8, s.getParentContact());
            ps.setString(9, s.getAddress());
            ps.setString(10, s.getStatus());
            ps.setInt(11, s.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update student: " + e.getMessage(), e);
        }
    }

    public boolean deleteStudent(int id) throws ResourceNotFoundException {
        if (dbConfig.isMockMode()) {
            boolean removed = mockStudents.removeIf(s -> s.getId() == id);
            if (!removed) throw new ResourceNotFoundException("Student", id);
            return true;
        }

        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new ResourceNotFoundException("Student", id);
            return true;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete student: " + e.getMessage(), e);
        }
    }

    public void updateAssignedRoom(int studentId, Integer roomId) {
        if (dbConfig.isMockMode()) {
            for (Student s : mockStudents) {
                if (s.getId() == studentId) {
                    s.setAssignedRoomId(roomId);
                    break;
                }
            }
            return;
        }

        String sql = "UPDATE students SET assigned_room_id = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (roomId == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, roomId);
            }
            ps.setInt(2, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update student room assignment: " + e.getMessage(), e);
        }
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setStudentId(rs.getString("student_id"));
        s.setFullName(rs.getString("full_name"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setDepartment(rs.getString("department"));
        s.setYearOfStudy(rs.getString("year_of_study"));
        Date dob = rs.getDate("dob");
        s.setDob(dob != null ? dob.toString() : "");
        s.setGender(rs.getString("gender"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        s.setParentContact(rs.getString("parent_contact"));
        s.setAddress(rs.getString("address"));
        int roomId = rs.getInt("assigned_room_id");
        if (!rs.wasNull()) {
            s.setAssignedRoomId(roomId);
            s.setAssignedRoomNumber(rs.getString("room_number"));
        }
        s.setStatus(rs.getString("status"));
        Date adm = rs.getDate("admission_date");
        s.setAdmissionDate(adm != null ? adm.toString() : "");
        return s;
    }
}
