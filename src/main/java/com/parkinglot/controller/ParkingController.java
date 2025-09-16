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
import org.springframework.boot.Banner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/parking")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@Slf4j
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        log.info("oidc user {} authenticated",oidcUser.getFullName());
        model.addAttribute("name", oidcUser.getFullName());
        model.addAttribute("email", oidcUser.getEmail());
        return "userhome";
    }

    @GetMapping("/createticket")
    public String createTicket() {
        return "createticket";
    }

    @GetMapping("/viewticket")
    public String viewticket(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        List<TicketDTO> tickets = parkingService.findByUser(getCurrentUser(oidcUser));
        model.addAttribute("tickets", tickets);
        return "viewticket";
    }



    @PostMapping("/entry")
    public String parkVehicle(
            @Valid @ModelAttribute VehicleEntryRequest request,
            @AuthenticationPrincipal OidcUser oidcUser, Model model) {

        log.info("vehile {} getting parked",request.getPlateNumber());
        User user = getCurrentUser(oidcUser);
        log.info("Vehicle entry request from user: {}", user.getUsername());

        VehicleEntryResponse response = parkingService.parkVehicle(request, user);
        model.addAttribute("ticket", response);
        return "ticket";
    }

    @PostMapping("/exitcalculate")
    public ResponseEntity<ApiResponse<VehicleExitResponse>> calculateExitFee(
            @Valid @RequestBody VehicleExitRequest request,
            @AuthenticationPrincipal OidcUser oidcUser) {

        User user = getCurrentUser(oidcUser);
        log.info("Exit fee calculation request from user: {}", user.getUsername());

        VehicleExitResponse response = parkingService.calculateExitFee(request, user);
        return ResponseEntity.ok(ApiResponse.success("Exit fee calculated", response));
    }

    @PostMapping("/payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal OidcUser oidcUser) {

        User user = getCurrentUser(oidcUser);
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

    private User getCurrentUser(OidcUser authentication) {
        log.info("getting authentication user with email {}",authentication.getEmail());
        return parkingService.findByUsername(authentication.getEmail()).orElse(null);
    }
}
