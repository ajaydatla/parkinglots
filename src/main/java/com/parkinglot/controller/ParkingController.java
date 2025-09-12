package com.parkinglot.controller;

import com.parkinglot.dto.*;
import com.parkinglot.entity.User;
import com.parkinglot.enums.UserRole;
import com.parkinglot.repository.UserRepository;
import com.parkinglot.service.ParkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
@Slf4j
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @PostMapping("/entry")
    public ResponseEntity<ApiResponse<VehicleEntryResponse>> parkVehicle(
            @Valid @RequestBody VehicleEntryRequest request,
            Authentication authentication) {

        User user = getCurrentUser(authentication);
        log.info("Vehicle entry request from user: {}", user.getUsername());

        VehicleEntryResponse response = parkingService.parkVehicle(request, user);
        return ResponseEntity.ok(ApiResponse.success("Vehicle parked successfully", response));
    }

    @PostMapping("/exitcalculate")
    public ResponseEntity<ApiResponse<VehicleExitResponse>> calculateExitFee(
            @Valid @RequestBody VehicleExitRequest request,
            Authentication authentication) {

        User user = getCurrentUser(authentication);
        log.info("Exit fee calculation request from user: {}", user.getUsername());

        VehicleExitResponse response = parkingService.calculateExitFee(request, user);
        return ResponseEntity.ok(ApiResponse.success("Exit fee calculated", response));
    }

    @PostMapping("/payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {

        User user = getCurrentUser(authentication);
        log.info("Payment request from user: {}", user.getUsername());

        PaymentResponse response = parkingService.processPayment(request, user);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", response));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<ParkingStatusResponse>> getParkingStatus(
            @RequestParam(required = false, defaultValue = "1") Long parkingLotId) {

        log.info("Parking status request for lot: {}", parkingLotId);

        ParkingStatusResponse response = parkingService.getParkingStatus(parkingLotId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private User getCurrentUser(Authentication authentication) {
        // Get the principal object from the authentication
        Object principal = authentication.getPrincipal();

        // Check if the principal is an instance of UserDetails
        if (principal instanceof UserDetails userDetails) {
            // Cast and return the UserDetails object
            return parkingService.findByUsername(userDetails.getUsername()).orElse(null);
        }

        // Throw an exception if the principal is of an unexpected type
        throw new IllegalArgumentException("Authentication principal is not a UserDetails object.");
    }
}
