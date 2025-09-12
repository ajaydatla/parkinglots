package com.parkinglot.strategy;

import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.enums.VehicleType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("nearestSlotStrategy")
public class NearestSlotAllocationStrategy implements SlotAllocationStrategy {

    @Override
    public Optional<ParkingSlot> allocateSlot(List<ParkingSlot> availableSlots, VehicleType vehicleType) {
        if (availableSlots.isEmpty()) {
            return Optional.empty();
        }

        // Slots are already sorted by floor and slot number in repository query
        // Return the first available slot (nearest to entrance)
        return availableSlots.stream()
                .filter(slot -> slot.getVehicleType() == vehicleType)
                .findFirst();
    }
}
