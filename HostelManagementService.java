package com.hostel.service;

import com.hostel.config.DatabaseConnection;
import com.hostel.dao.*;
import com.hostel.exception.*;
import com.hostel.model.*;

import java.util.*;

/**
 * Service Implementation: HostelManagementService
 * Implements the HostelOperations interface.
 * Demonstrates:
 * 1. Polymorphism: Implements abstract interface methods.
 * 2. Encapsulation & Separation of Concerns: Coordinates DAOs, validates business invariants.
 * 3. Exception Handling: Catches, converts, and throws appropriate custom exceptions.
 */
public class HostelManagementService implements HostelOperations {
    private final StudentDAO studentDAO;
    private final RoomDAO roomDAO;
    private final PaymentDAO paymentDAO;
    private final ComplaintDAO complaintDAO;
    private final AllocationDAO allocationDAO;

    public HostelManagementService() {
        this.studentDAO = new StudentDAO();
        this.roomDAO = new RoomDAO();
        this.paymentDAO = new PaymentDAO();
        this.complaintDAO = new ComplaintDAO();
        this.allocationDAO = new AllocationDAO();
    }

    // ==========================================
    // STUDENT MANAGEMENT
    // ==========================================
    @Override
    public Student registerStudent(Student student) throws HostelException {
        if (student.getFullName() == null || student.getFullName().trim().isEmpty()) {
            throw new HostelException("STUDENT_NAME_REQUIRED", "Student full name is mandatory.");
        }
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            throw new HostelException("STUDENT_ID_REQUIRED", "Student ID is mandatory.");
        }
        if (student.getRollNumber() == null || student.getRollNumber().trim().isEmpty()) {
            throw new HostelException("ROLL_NUMBER_REQUIRED", "Roll number is mandatory.");
        }

        return studentDAO.addStudent(student);
    }

    @Override
    public boolean updateStudent(Student student) throws HostelException {
        return studentDAO.updateStudent(student);
    }

    @Override
    public boolean removeStudent(int studentId) throws HostelException {
        Student s = studentDAO.getStudentById(studentId);
        if (s.getAssignedRoomId() != null && s.getAssignedRoomId() > 0) {
            // Vacate room allocation first
            vacateRoom(studentId);
        }
        return studentDAO.deleteStudent(studentId);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    @Override
    public Student getStudentById(int id) throws HostelException {
        return studentDAO.getStudentById(id);
    }

    // ==========================================
    // ROOM MANAGEMENT
    // ==========================================
    @Override
    public Room createRoom(Room room) throws HostelException {
        if (room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()) {
            throw new HostelException("ROOM_NUM_REQUIRED", "Room number is required.");
        }
        if (room.getCapacity() <= 0) {
            throw new HostelException("INVALID_CAPACITY", "Room capacity must be greater than 0.");
        }
        return roomDAO.addRoom(room);
    }

    @Override
    public boolean updateRoom(Room room) throws HostelException {
        return roomDAO.updateRoom(room);
    }

    @Override
    public boolean deleteRoom(int roomId) throws HostelException {
        Room r = roomDAO.getRoomById(roomId);
        if (r.getOccupiedBeds() > 0) {
            throw new RoomAllocationException("Cannot delete room " + r.getRoomNumber() + " because it currently has " + r.getOccupiedBeds() + " occupied bed(s). Please vacate students first.");
        }
        return roomDAO.deleteRoom(roomId);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    @Override
    public Room getRoomById(int id) throws HostelException {
        return roomDAO.getRoomById(id);
    }

    // ==========================================
    // ROOM ALLOCATION
    // ==========================================
    @Override
    public RoomAllocation allocateRoom(int studentId, int roomId, String remarks) throws HostelException {
        Student student = studentDAO.getStudentById(studentId);
        if (student.getAssignedRoomId() != null && student.getAssignedRoomId() > 0) {
            throw new RoomAllocationException("Student " + student.getFullName() + " is already assigned to a room. Please vacate first.");
        }

        Room room = roomDAO.getRoomById(roomId);
        if ("Maintenance".equalsIgnoreCase(room.getStatus())) {
            throw new RoomAllocationException("Room " + room.getRoomNumber() + " is currently under Maintenance and cannot be allocated.");
        }
        if (room.isFull()) {
            throw new RoomAllocationException("Room " + room.getRoomNumber() + " is fully occupied (" + room.getOccupiedBeds() + "/" + room.getCapacity() + " beds).");
        }

        // 1. Update Student record
        studentDAO.updateAssignedRoom(studentId, roomId);

        // 2. Adjust Room occupancy
        roomDAO.adjustOccupancy(roomId, +1);

        // 3. Create Allocation Audit Record
        RoomAllocation alloc = new RoomAllocation();
        alloc.setStudentId(studentId);
        alloc.setStudentName(student.getFullName());
        alloc.setStudentRollNo(student.getRollNumber());
        alloc.setRoomId(roomId);
        alloc.setRoomNumber(room.getRoomNumber());
        alloc.setRoomType(room.getRoomType());
        alloc.setRemarks(remarks != null && !remarks.isEmpty() ? remarks : "Allocated to room " + room.getRoomNumber());

        return allocationDAO.createAllocation(alloc);
    }

    @Override
    public boolean vacateRoom(int studentId) throws HostelException {
        Student student = studentDAO.getStudentById(studentId);
        Integer roomId = student.getAssignedRoomId();

        if (roomId == null || roomId <= 0) {
            throw new RoomAllocationException("Student " + student.getFullName() + " does not have an active room allocation.");
        }

        // 1. Decrement Room occupancy
        roomDAO.adjustOccupancy(roomId, -1);

        // 2. Unassign student
        studentDAO.updateAssignedRoom(studentId, null);

        // 3. Mark Allocation as Vacated
        allocationDAO.vacateAllocation(studentId, roomId);

        return true;
    }

    @Override
    public List<RoomAllocation> getAllAllocations() {
        return allocationDAO.getAllAllocations();
    }

    // ==========================================
    // FEE MANAGEMENT
    // ==========================================
    @Override
    public Payment recordFeePayment(Payment payment) throws HostelException {
        if (payment.getTotalAmount() <= 0) {
            throw new HostelException("INVALID_FEE_AMOUNT", "Total fee amount must be positive.");
        }
        if (payment.getAmountPaid() < 0) {
            throw new HostelException("INVALID_PAID_AMOUNT", "Amount paid cannot be negative.");
        }

        payment.setPendingAmount(Payment.calculatePending(payment.getTotalAmount(), payment.getAmountPaid()));
        payment.setPaymentStatus(Payment.resolveStatus(payment.getTotalAmount(), payment.getAmountPaid()));

        return paymentDAO.recordPayment(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentDAO.getAllPayments();
    }

    // ==========================================
    // COMPLAINT MANAGEMENT
    // ==========================================
    @Override
    public Complaint registerComplaint(Complaint complaint) throws HostelException {
        if (complaint.getTitle() == null || complaint.getTitle().trim().isEmpty()) {
            throw new HostelException("TITLE_REQUIRED", "Complaint title is required.");
        }
        if (complaint.getDescription() == null || complaint.getDescription().trim().isEmpty()) {
            throw new HostelException("DESC_REQUIRED", "Complaint description is required.");
        }
        return complaintDAO.addComplaint(complaint);
    }

    @Override
    public boolean updateComplaintStatus(int complaintId, String status, String remarks) throws HostelException {
        return complaintDAO.updateStatus(complaintId, status, remarks);
    }

    @Override
    public List<Complaint> getAllComplaints() {
        return complaintDAO.getAllComplaints();
    }

    // ==========================================
    // DASHBOARD & ANALYTICS METRICS
    // ==========================================
    @Override
    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        List<Student> students = studentDAO.getAllStudents();
        List<Room> rooms = roomDAO.getAllRooms();
        List<Payment> payments = paymentDAO.getAllPayments();
        List<Complaint> complaints = complaintDAO.getAllComplaints();

        int totalStudents = students.size();
        int totalRooms = rooms.size();
        int occupiedRooms = 0;
        int availableRooms = 0;
        int maintenanceRooms = 0;
        int totalCapacityBeds = 0;
        int totalOccupiedBeds = 0;

        Map<String, Integer> roomTypeDistribution = new HashMap<>();

        for (Room r : rooms) {
            totalCapacityBeds += r.getCapacity();
            totalOccupiedBeds += r.getOccupiedBeds();

            if ("Maintenance".equalsIgnoreCase(r.getStatus())) {
                maintenanceRooms++;
            } else if (r.isFull()) {
                occupiedRooms++;
            } else {
                availableRooms++;
            }

            roomTypeDistribution.put(r.getRoomType(), roomTypeDistribution.getOrDefault(r.getRoomType(), 0) + 1);
        }

        int availableBeds = Math.max(0, totalCapacityBeds - totalOccupiedBeds);
        double occupancyPercentage = totalCapacityBeds > 0 ? ((double) totalOccupiedBeds / totalCapacityBeds) * 100 : 0.0;

        double totalCollected = 0.0;
        double totalPending = 0.0;
        for (Payment p : payments) {
            totalCollected += p.getAmountPaid();
            totalPending += p.getPendingAmount();
        }

        int openComplaints = 0;
        int resolvedComplaints = 0;
        for (Complaint c : complaints) {
            if ("Resolved".equalsIgnoreCase(c.getStatus())) {
                resolvedComplaints++;
            } else {
                openComplaints++;
            }
        }

        metrics.put("totalStudents", totalStudents);
        metrics.put("totalRooms", totalRooms);
        metrics.put("occupiedRooms", occupiedRooms);
        metrics.put("availableRooms", availableRooms);
        metrics.put("maintenanceRooms", maintenanceRooms);
        metrics.put("totalCapacityBeds", totalCapacityBeds);
        metrics.put("totalOccupiedBeds", totalOccupiedBeds);
        metrics.put("availableBeds", availableBeds);
        metrics.put("occupancyPercentage", Math.round(occupancyPercentage * 10.0) / 10.0);
        metrics.put("totalFeesCollected", totalCollected);
        metrics.put("totalPendingFees", totalPending);
        metrics.put("openComplaints", openComplaints);
        metrics.put("resolvedComplaints", resolvedComplaints);
        metrics.put("roomTypeDistribution", roomTypeDistribution);
        metrics.put("dbStatus", DatabaseConnection.getInstance().getStatusMessage());

        return metrics;
    }
}
