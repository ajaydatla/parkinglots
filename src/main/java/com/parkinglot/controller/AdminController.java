package com.parkinglot.controller;

import com.parkinglot.dto.ApiResponse;
import com.parkinglot.dto.ParkingSlotRequest;
import com.parkinglot.dto.PricingRuleRequest;
import com.parkinglot.dto.TicketDTO;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.PricingRule;
import com.parkinglot.entity.Ticket;
import com.parkinglot.entity.User;
import com.parkinglot.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public String getAdminHome(){
        return "admin";
    }

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        log.info("oidc user {} authenticated",oidcUser.getFullName());
        model.addAttribute("name", oidcUser.getFullName());
        model.addAttribute("email", oidcUser.getEmail());
        return "adminhome";
    }

    // Parking Slot Management
    @PostMapping("/slots")
    public ResponseEntity<ApiResponse<ParkingSlot>> addParkingSlot(
            @Valid @RequestBody ParkingSlotRequest request) {

        log.info("Adding parking slot: {}", request.getSlotNumber());
        ParkingSlot slot = adminService.addParkingSlot(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Parking slot added successfully", slot));
    }

    @DeleteMapping("/slots/{slotId}")
    public ResponseEntity<ApiResponse<Void>> removeParkingSlot(@PathVariable Long slotId) {
        log.info("Removing parking slot: {}", slotId);
        adminService.removeParkingSlot(slotId);
        return ResponseEntity.ok(ApiResponse.success("Parking slot removed successfully", null));
    }

    @GetMapping("/slots")
    public String getAllSlots(
            @RequestParam(defaultValue = "1") Long parkingLotId, Model model) {

        log.info("Fetching all slots for parking lot: {}", parkingLotId);
        List<ParkingSlot> slots = adminService.getAllSlots(parkingLotId);
        model.addAttribute("slots", slots);
        return "slots";
    }

//    @GetMapping("/tickets")
//    @ResponseBody
//    public ResponseEntity<ApiResponse<List<TicketDTO>>> getAllTickets(Model model) {
//
//        log.info("Fetching all tickets for parking lot");
//        List<TicketDTO> tickets = adminService.getAllTickets();
//        model.addAttribute("ticket", tickets);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.success("Pricing rule created successfully", tickets));
//    }

    @GetMapping("/tickets")
    public String getAllTickets(Model model) {

        log.info("Fetching all tickets for parking lot");
        List<TicketDTO> tickets = adminService.getAllTickets();
        model.addAttribute("tickets", tickets);
        return "tickets";
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {

        log.info("Fetching all users for parking lot");
        List<User> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    // Pricing Rule Management
    @PostMapping("/pricing-rules")
    public ResponseEntity<ApiResponse<PricingRule>> createPricingRule(
            @Valid @RequestBody PricingRuleRequest request) {

        log.info("Creating pricing rule for vehicle type: {}", request.getVehicleType());
        PricingRule rule = adminService.createPricingRule(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pricing rule created successfully", rule));
    }

    @PutMapping("/pricing-rules/{ruleId}")
    public ResponseEntity<ApiResponse<PricingRule>> updatePricingRule(
            @PathVariable Long ruleId,
            @Valid @RequestBody PricingRuleRequest request) {

        log.info("Updating pricing rule: {}", ruleId);
        PricingRule rule = adminService.updatePricingRule(ruleId, request);
        return ResponseEntity.ok(ApiResponse.success("Pricing rule updated successfully", rule));
    }

    @DeleteMapping("/pricing-rules/{ruleId}")
    public ResponseEntity<ApiResponse<Void>> deletePricingRule(@PathVariable Long ruleId) {
        log.info("Deleting pricing rule: {}", ruleId);
        adminService.deletePricingRule(ruleId);
        return ResponseEntity.ok(ApiResponse.success("Pricing rule deleted successfully", null));
    }

    @GetMapping("/pricing-rules")
    public ResponseEntity<ApiResponse<List<PricingRule>>> getAllPricingRules(
            @RequestParam(defaultValue = "1") Long parkingLotId) {

        log.info("Fetching all pricing rules for parking lot: {}", parkingLotId);
        List<PricingRule> rules = adminService.getAllPricingRules(parkingLotId);
        return ResponseEntity.ok(ApiResponse.success(rules));
    }
}
