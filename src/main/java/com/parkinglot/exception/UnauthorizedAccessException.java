package com.parkinglot.exception;

public class UnauthorizedAccessException extends ParkingException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
