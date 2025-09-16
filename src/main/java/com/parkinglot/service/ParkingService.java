package com.parkinglot.service;

import com.parkinglot.dto.*;
import com.parkinglot.entity.Ticket;
import com.parkinglot.entity.User;

import java.util.List;
import java.util.Optional;

public interface ParkingService {
    VehicleEntryResponse parkVehicle(VehicleEntryRequest request, User user);
    VehicleExitResponse calculateExitFee(VehicleExitRequest request, User user);
    PaymentResponse processPayment(PaymentRequest request, User user);
    ParkingStatusResponse getParkingStatus(Long parkingLotId);
    Optional<User> findByUsername(String username);
    List<TicketDTO> findByUser(User user);
}
