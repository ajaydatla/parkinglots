package com.parkinglot.strategy;

import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.enums.VehicleType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component("randomSlotStrategy")
public class RandomSlotAllocationStrategy implements SlotAllocationStrategy {

    private final Random random = new Random();

    @Override
    public Optional<ParkingSlot> allocateSlot(List<ParkingSlot> availableSlots, VehicleType vehicleType) {
        List<ParkingSlot> compatibleSlots = availableSlots.stream()
                .filter(slot -> slot.getVehicleType() == vehicleType)
                .toList();

        if (compatibleSlots.isEmpty()) {
            return Optional.empty();
        }

        // Select a random slot from available compatible slots
        int randomIndex = random.nextInt(compatibleSlots.size());
        return Optional.of(compatibleSlots.get(randomIndex));
    }
}
