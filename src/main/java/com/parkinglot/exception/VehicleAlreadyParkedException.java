package com.parkinglot.exception;

public class VehicleAlreadyParkedException extends ParkingException {
    public VehicleAlreadyParkedException(String message) {
        super(message);
    }
}
