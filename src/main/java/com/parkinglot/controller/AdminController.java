package com.parkinglot.controller;

import com.parkinglot.dto.ApiResponse;
import com.parkinglot.dto.ParkingSlotRequest;
import com.parkinglot.dto.PricingRuleRequest;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.PricingRule;
import com.parkinglot.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminController {

    private final AdminService adminService;

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
    public ResponseEntity<ApiResponse<List<ParkingSlot>>> getAllSlots(
            @RequestParam(defaultValue = "1") Long parkingLotId) {

        log.info("Fetching all slots for parking lot: {}", parkingLotId);
        List<ParkingSlot> slots = adminService.getAllSlots(parkingLotId);
        return ResponseEntity.ok(ApiResponse.success(slots));
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
