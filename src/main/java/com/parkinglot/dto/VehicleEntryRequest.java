package com.parkinglot.dto;

import com.parkinglot.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleEntryRequest {
    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    private Long parkingLotId; // Optional, will use default if not provided
}
