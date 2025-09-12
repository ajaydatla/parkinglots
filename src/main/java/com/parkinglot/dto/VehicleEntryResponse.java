package com.parkinglot.dto;

import com.parkinglot.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleEntryResponse {
    private String ticketNumber;
    private String plateNumber;
    private VehicleType vehicleType;
    private String slotNumber;
    private Integer floorNumber;
    private LocalDateTime entryTime;
    private String message;
}
