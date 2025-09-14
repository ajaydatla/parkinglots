package com.parkinglot.strategy;

import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.enums.VehicleType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("floorWiseStrategy")
public class FloorWiseSlotAllocationStrategy implements SlotAllocationStrategy {

    @Override
    public Optional<ParkingSlot> allocateSlot(List<ParkingSlot> availableSlots, VehicleType vehicleType) {
        // Group slots by floor
        Map<Integer, List<ParkingSlot>> slotsByFloor = availableSlots.stream()
                .filter(slot -> slot.getVehicleType() == vehicleType)
                .collect(Collectors.groupingBy(ParkingSlot::getFloorNumber));

        if (slotsByFloor.isEmpty()) {
            return Optional.empty();
        }

        // Find the floor with the most available slots
        Integer bestFloor = slotsByFloor.entrySet().stream()
                .max(Map.Entry.<Integer, List<ParkingSlot>>comparingByValue(
                        (list1, list2) -> Integer.compare(list1.size(), list2.size())))
                .map(Map.Entry::getKey)
                .orElse(null);

        if (bestFloor != null && !slotsByFloor.get(bestFloor).isEmpty()) {
            return Optional.of(slotsByFloor.get(bestFloor).get(0));
        }

        return Optional.empty();
    }
}
