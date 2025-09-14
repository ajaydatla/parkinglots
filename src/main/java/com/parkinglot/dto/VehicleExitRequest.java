package com.parkinglot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleExitRequest {
    @NotBlank(message = "Ticket number is required")
    private String ticketNumber;
}
