package com.hostel.exception;

/**
 * Base custom checked exception for the Hostel Management System.
 * Demonstrates custom exception hierarchy and checked exception handling in Java OOPS.
 */
public class HostelException extends Exception {
    private final String errorCode;

    public HostelException(String message) {
        super(message);
        this.errorCode = "HOSTEL_ERR_GENERIC";
    }

    public HostelException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public HostelException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "HOSTEL_ERR_WRAPPED";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
