package com.parkinglot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingStatusResponse {
    private Long parkingLotId;
    private String parkingLotName;
    private Map<String, Integer> availableSlots; // VehicleType -> Count
    private Map<String, Integer> occupiedSlots; // VehicleType -> Count
    private Map<String, Integer> totalSlots; // VehicleType -> Count
    private Integer totalFloors;
}
