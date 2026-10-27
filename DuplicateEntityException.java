package com.hostel.exception;

/**
 * Thrown when trying to insert an entity with duplicate unique keys (Roll No, Student ID, Room Number).
 */
public class DuplicateEntityException extends HostelException {
    public DuplicateEntityException(String fieldName, String value) {
        super("DUPLICATE_ENTRY", "An entry with " + fieldName + " '" + value + "' already exists in the system.");
    }
}
