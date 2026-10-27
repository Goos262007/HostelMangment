package com.hostel.exception;

/**
 * Thrown when a requested resource (Student, Room, Payment, Complaint) is not found.
 */
public class ResourceNotFoundException extends HostelException {
    public ResourceNotFoundException(String resourceType, Object identifier) {
        super("NOT_FOUND", resourceType + " with identifier '" + identifier + "' was not found.");
    }

    public ResourceNotFoundException(String message) {
        super("NOT_FOUND", message);
    }
}
