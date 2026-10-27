package com.hostel.exception;

/**
 * Thrown when business rules for room allocation are violated:
 * - Room is already at full capacity
 * - Room is currently under maintenance
 * - Student is already allocated to another room
 */
public class RoomAllocationException extends HostelException {
    public RoomAllocationException(String message) {
        super("ROOM_ALLOCATION_VIOLATION", message);
    }
}
