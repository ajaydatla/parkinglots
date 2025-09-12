package com.parkinglot.enums;

public enum VehicleType {
    BIKE(1),
    CAR(2),
    TRUCK(3);

    private final int size;

    VehicleType(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
