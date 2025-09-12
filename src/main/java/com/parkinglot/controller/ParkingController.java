package com.parkinglot.controller;

import com.parkinglot.dto.ApiResponse;
import com.parkinglot.entity.ParkingLot;
import com.parkinglot.service.ParkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parking")
//@RequiredArgsConstructor
//@Slf4j
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @GetMapping("/getAllParkingLOTS")
    public ResponseEntity<ApiResponse<List<ParkingLot>>> healthCheck() {

        return ResponseEntity.ok(ApiResponse.success("Service is healthy", parkingService.findAl()));
    }

//    private final ParkingService parkingService;
//    private final UserRepository userRepository;
//
//    @PostMapping("/entry")
//    public ResponseEntity<ApiResponse<VehicleEntryResponse>> parkVehicle(
//            @Valid @RequestBody VehicleEntryRequest request,
//            Authentication authentication) {
//
//        User user = getCurrentUser(authentication);
//        log.info("Vehicle entry request from user: {}", user.getEmail());
//
//        VehicleEntryResponse response = parkingService.parkVehicle(request, user);
//        return ResponseEntity.ok(ApiResponse.success("Vehicle parked successfully", response));
//    }
//
//    @PostMapping("/exit/calculate")
//    public ResponseEntity<ApiResponse<VehicleExitResponse>> calculateExitFee(
//            @Valid @RequestBody VehicleExitRequest request,
//            Authentication authentication) {
//
//        User user = getCurrentUser(authentication);
//        log.info("Exit fee calculation request from user: {}", user.getEmail());
//
//        VehicleExitResponse response = parkingService.calculateExitFee(request, user);
//        return ResponseEntity.ok(ApiResponse.success("Exit fee calculated", response));
//    }
//
//    @PostMapping("/payment")
//    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
//            @Valid @RequestBody PaymentRequest request,
//            Authentication authentication) {
//
//        User user = getCurrentUser(authentication);
//        log.info("Payment request from user: {}", user.getEmail());
//
//        PaymentResponse response = parkingService.processPayment(request, user);
//        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", response));
//    }
//
//    @GetMapping("/status")
//    public ResponseEntity<ApiResponse<ParkingStatusResponse>> getParkingStatus(
//            @RequestParam(required = false, defaultValue = "1") Long parkingLotId) {
//
//        log.info("Parking status request for lot: {}", parkingLotId);
//
//        ParkingStatusResponse response = parkingService.getParkingStatus(parkingLotId);
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
//
//    private User getCurrentUser(Authentication authentication) {
//        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
//        String email = oauth2User.getAttribute("email");
//
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found: " + email));
//    }
}
