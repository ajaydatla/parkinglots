package com.parkinglot.dto;

import com.parkinglot.enums.VehicleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingRuleRequest {
    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotNull(message = "Free hours is required")
    @PositiveOrZero(message = "Free hours cannot be negative")
    private Integer freeHours;

    @NotNull(message = "Hourly rate is required")
    @PositiveOrZero(message = "Hourly rate cannot be negative")
    private BigDecimal hourlyRate;

    @PositiveOrZero(message = "Daily max rate cannot be negative")
    private BigDecimal dailyMaxRate;

    @NotNull(message = "Parking lot ID is required")
    private Long parkingLotId;
}
