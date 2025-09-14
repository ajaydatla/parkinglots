package com.parkinglot.dto;

import com.parkinglot.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlotRequest {
    @NotBlank(message = "Slot number is required")
    private String slotNumber;

    @NotNull(message = "Floor number is required")
    @Positive(message = "Floor number must be positive")
    private Integer floorNumber;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotNull(message = "Parking lot ID is required")
    private Long parkingLotId;
}
