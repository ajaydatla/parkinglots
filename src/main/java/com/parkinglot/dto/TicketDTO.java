package com.parkinglot.dto;

import com.parkinglot.entity.Payment;
import com.parkinglot.enums.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketDTO(
    Long id,
    String ticketNumber,
    String plateNumber,
    String slotNumber,
    LocalDateTime entryTime,
    LocalDateTime exitTime,
    TicketStatus status,
    String user,
    BigDecimal amount
) {}
