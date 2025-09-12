package com.parkinglot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleExitResponse {
    private String ticketNumber;
    private String plateNumber;
    private BigDecimal totalAmount;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private long totalHours;
    private String message;
}
