package com.hostel.dao;

import com.hostel.config.DatabaseConnection;
import com.hostel.exception.DatabaseException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data Access Object: PaymentDAO
 * Handles fee transaction persistence and balance queries.
 */
public class PaymentDAO {
    private final DatabaseConnection dbConfig = DatabaseConnection.getInstance();
    private static final List<Payment> mockPayments = new CopyOnWriteArrayList<>();
    private static final AtomicInteger mockIdSeq = new AtomicInteger(10);

    static {
        // Initialize default mock payments
        Payment p1 = new Payment(1, "REC-2024-001", 1, 57000.0, 57000.0, "2024-08-05", "UPI", "UPI/2024/908129841", "Full semester fee paid");
        p1.setStudentName("Aarav Mehta"); p1.setStudentRollNo("21CSE014");
        Payment p2 = new Payment(2, "REC-2024-002", 2, 42000.0, 42000.0, "2024-08-06", "Net Banking", "HDFC-NB-8823192", "Annual maintenance and mess paid");
        p2.setStudentName("Priya Sharma"); p2.setStudentRollNo("22IT045");
        Payment p3 = new Payment(3, "REC-2024-003", 3, 42000.0, 25000.0, "2024-08-07", "UPI", "UPI/2024/778102931", "1st installment paid, balance due in 30 days");
        p3.setStudentName("Rohan Verma"); p3.setStudentRollNo("21ECE028");
        Payment p4 = new Payment(4, "REC-2024-004", 4, 42000.0, 42000.0, "2024-08-08", "Credit/Debit Card", "TXN-CARD-449102", "Card swipe at counter");
        p4.setStudentName("Ananya Patel"); p4.setStudentRollNo("23CSE089");
        Payment p5 = new Payment(5, "REC-2024-005", 5, 33000.0, 33000.0, "2024-08-09", "UPI", "UPI/2024/339102941", "Semester accommodation fees");
        p5.setStudentName("Vikram Singh"); p5.setStudentRollNo("20ME003");
        Payment p6 = new Payment(6, "REC-2024-006", 6, 33000.0, 20000.0, "2024-08-10", "Net Banking", "ICICI-NB-1092834", "Part payment received");
        p6.setStudentName("Sneha Kulkarni"); p6.setStudentRollNo("22CSE056");
        Payment p7 = new Payment(7, "REC-2024-007", 7, 33000.0, 33000.0, "2024-08-10", "Cash", "CASH-REC-1082", "Cash deposited at hostel cashier");
        p7.setStudentName("Kunal Reddy"); p7.setStudentRollNo("21CIV012");
        Payment p8 = new Payment(8, "REC-2024-008", 8, 33000.0, 0.0, "2024-08-12", "Net Banking", "PENDING", "Fee invoice generated");
        p8.setStudentName("Tanvi Deshmukh"); p8.setStudentRollNo("23IT019");
        Payment p9 = new Payment(9, "REC-2024-009", 9, 33000.0, 33000.0, "2024-08-12", "UPI", "UPI/2024/119284019", "Full clearance issued");
        p9.setStudentName("Aditya Nair"); p9.setStudentRollNo("21ECE091");
        Payment p10 = new Payment(10, "REC-2024-010", 10, 66000.0, 40000.0, "2024-08-14", "Cheque", "CHQ-SBI-991204", "Cheque cleared, 2nd installment pending");
        p10.setStudentName("Ishaan Gupta"); p10.setStudentRollNo("23CSE110");

        mockPayments.add(p1); mockPayments.add(p2); mockPayments.add(p3); mockPayments.add(p4);
        mockPayments.add(p5); mockPayments.add(p6); mockPayments.add(p7); mockPayments.add(p8);
        mockPayments.add(p9); mockPayments.add(p10);
    }

    public List<Payment> getAllPayments() {
        if (dbConfig.isMockMode()) {
            return new ArrayList<>(mockPayments);
        }

        List<Payment> list = new ArrayList<>();
        String sql = "SELECT f.*, s.full_name, s.roll_number " +
                     "FROM fees f " +
                     "JOIN students s ON f.student_id = s.id " +
                     "ORDER BY f.id DESC";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Payment p = new Payment();
                p.setId(rs.getInt("id"));
                p.setReceiptNo(rs.getString("receipt_no"));
                p.setStudentId(rs.getInt("student_id"));
                p.setStudentName(rs.getString("full_name"));
                p.setStudentRollNo(rs.getString("roll_number"));
                p.setTotalAmount(rs.getDouble("total_amount"));
                p.setAmountPaid(rs.getDouble("amount_paid"));
                p.setPendingAmount(rs.getDouble("pending_amount"));
                Date d = rs.getDate("payment_date");
                p.setPaymentDate(d != null ? d.toString() : "");
                p.setPaymentMethod(rs.getString("payment_method"));
                p.setTransactionId(rs.getString("transaction_id"));
                p.setPaymentStatus(rs.getString("payment_status"));
                p.setRemarks(rs.getString("remarks"));
                list.add(p);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch payments: " + e.getMessage(), e);
        }
        return list;
    }

    public Payment recordPayment(Payment p) {
        if (p.getReceiptNo() == null || p.getReceiptNo().isEmpty()) {
            p.setReceiptNo("REC-" + System.currentTimeMillis() % 1000000);
        }

        if (dbConfig.isMockMode()) {
            p.setId(mockIdSeq.incrementAndGet());
            mockPayments.add(0, p);
            return p;
        }

        String sql = "INSERT INTO fees (receipt_no, student_id, total_amount, amount_paid, pending_amount, payment_date, payment_method, transaction_id, payment_status, remarks) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getReceiptNo());
            ps.setInt(2, p.getStudentId());
            ps.setDouble(3, p.getTotalAmount());
            ps.setDouble(4, p.getAmountPaid());
            ps.setDouble(5, p.getPendingAmount());
            ps.setDate(6, Date.valueOf(p.getPaymentDate() != null && !p.getPaymentDate().isEmpty() ? p.getPaymentDate() : java.time.LocalDate.now().toString()));
            ps.setString(7, p.getPaymentMethod());
            ps.setString(8, p.getTransactionId());
            ps.setString(9, p.getPaymentStatus());
            ps.setString(10, p.getRemarks());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
            return p;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to record payment in database: " + e.getMessage(), e);
        }
    }
}
