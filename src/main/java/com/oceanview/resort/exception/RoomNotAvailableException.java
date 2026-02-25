package com.oceanview.resort.exception;

/**
 * Exception thrown when a requested room is not available for the specified dates.
 */
public class RoomNotAvailableException extends RuntimeException {

    public RoomNotAvailableException(String message) {
        super(message);
    }

    public RoomNotAvailableException(String roomId, String dateRange) {
        super(String.format("Room '%s' is not available for %s", roomId, dateRange));
    }
}
