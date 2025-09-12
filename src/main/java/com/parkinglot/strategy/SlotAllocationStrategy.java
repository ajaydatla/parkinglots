package com.parkinglot.strategy;

import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.enums.VehicleType;

import java.util.List;
import java.util.Optional;

public interface SlotAllocationStrategy {
    Optional<ParkingSlot> allocateSlot(List<ParkingSlot> availableSlots, VehicleType vehicleType);
}
