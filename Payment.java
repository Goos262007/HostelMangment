package com.hostel.model;

/**
 * Model Class: Payment
 * Demonstrates:
 * 1. Encapsulation: Protects monetary transactions.
 * 2. Method Overloading (Compile-Time Polymorphism): Multiple calculatePending methods.
 */
public class Payment {
    private int id;
    private String receiptNo;
    private int studentId;
    private String studentName;
    private String studentRollNo;
    private double totalAmount;
    private double amountPaid;
    private double pendingAmount;
    private String paymentDate;
    private String paymentMethod; // 'Cash', 'UPI', 'Net Banking', 'Credit/Debit Card', 'Cheque'
    private String transactionId;
    private String paymentStatus; // 'Paid', 'Partially Paid', 'Pending'
    private String remarks;

    public Payment() {
        this.paymentMethod = "UPI";
        this.paymentStatus = "Pending";
    }

    public Payment(int id, String receiptNo, int studentId, double totalAmount,
                   double amountPaid, String paymentDate, String paymentMethod,
                   String transactionId, String remarks) {
        this.id = id;
        this.receiptNo = receiptNo;
        this.studentId = studentId;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.pendingAmount = calculatePending(totalAmount, amountPaid);
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.remarks = remarks;
        this.paymentStatus = resolveStatus(this.totalAmount, this.amountPaid);
    }

    // Method Overloading 1: Standard balance calculation
    public static double calculatePending(double total, double paid) {
        return Math.max(0.0, total - paid);
    }

    // Method Overloading 2: Balance calculation with late fee adjustment (OOPS Polymorphism)
    public static double calculatePending(double total, double paid, double lateFine) {
        return Math.max(0.0, (total + lateFine) - paid);
    }

    public static String resolveStatus(double total, double paid) {
        if (paid >= total && total > 0) {
            return "Paid";
        } else if (paid > 0 && paid < total) {
            return "Partially Paid";
        } else {
            return "Pending";
        }
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        this.pendingAmount = calculatePending(this.totalAmount, this.amountPaid);
        this.paymentStatus = resolveStatus(this.totalAmount, this.amountPaid);
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
        this.pendingAmount = calculatePending(this.totalAmount, this.amountPaid);
        this.paymentStatus = resolveStatus(this.totalAmount, this.amountPaid);
    }

    public double getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(double pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
