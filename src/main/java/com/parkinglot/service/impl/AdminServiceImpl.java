package com.parkinglot.service.impl;

import com.parkinglot.dto.ParkingSlotRequest;
import com.parkinglot.dto.PricingRuleRequest;
import com.parkinglot.dto.TicketDTO;
import com.parkinglot.entity.*;
import com.parkinglot.enums.SlotStatus;
import com.parkinglot.exception.ParkingException;
import com.parkinglot.repository.*;
import com.parkinglot.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public ParkingSlot addParkingSlot(ParkingSlotRequest request) {
        log.info("Adding parking slot: {} on floor {} for parking lot {}", 
                request.getSlotNumber(), request.getFloorNumber(), request.getParkingLotId());

        // Validate parking lot exists
        ParkingLot parkingLot = parkingLotRepository.findByIdAndActiveTrue(request.getParkingLotId())
                .orElseThrow(() -> new ParkingException("Parking lot not found: " + request.getParkingLotId()));

        // Check if slot already exists
        parkingSlotRepository.findBySlotNumberAndParkingLotId(request.getSlotNumber(), request.getParkingLotId())
                .ifPresent(slot -> {
                    throw new ParkingException("Slot " + request.getSlotNumber() + " already exists in this parking lot");
                });

        // Validate floor number
        if (request.getFloorNumber() > parkingLot.getTotalFloors()) {
            throw new ParkingException("Floor number " + request.getFloorNumber() + 
                    " exceeds total floors in parking lot: " + parkingLot.getTotalFloors());
        }

        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber(request.getSlotNumber());
        slot.setFloorNumber(request.getFloorNumber());
        slot.setVehicleType(request.getVehicleType());
        slot.setStatus(SlotStatus.AVAILABLE);
        slot.setParkingLot(parkingLot);

        slot = parkingSlotRepository.save(slot);
        log.info("Parking slot added successfully with ID: {}", slot.getId());

        return slot;
    }

    @Override
    @Transactional
    public void removeParkingSlot(Long slotId) {
        log.info("Removing parking slot with ID: {}", slotId);

        ParkingSlot slot = parkingSlotRepository.findById(slotId)
                .orElseThrow(() -> new ParkingException("Parking slot not found: " + slotId));

        if (slot.getStatus() == SlotStatus.OCCUPIED) {
            throw new ParkingException("Cannot remove occupied parking slot: " + slotId);
        }

        parkingSlotRepository.delete(slot);
        log.info("Parking slot removed successfully: {}", slotId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingSlot> getAllSlots(Long parkingLotId) {
        parkingLotRepository.findByIdAndActiveTrue(parkingLotId)
                .orElseThrow(() -> new ParkingException("Parking lot not found: " + parkingLotId));

        return parkingSlotRepository.findByParkingLotId(parkingLotId);
    }

    @Override
    @Transactional
    public PricingRule createPricingRule(PricingRuleRequest request) {
        log.info("Creating pricing rule for vehicle type: {} in parking lot: {}", 
                request.getVehicleType(), request.getParkingLotId());

        // Validate parking lot exists
        ParkingLot parkingLot = parkingLotRepository.findByIdAndActiveTrue(request.getParkingLotId())
                .orElseThrow(() -> new ParkingException("Parking lot not found: " + request.getParkingLotId()));

        // Check if pricing rule already exists for this vehicle type and parking lot
        pricingRuleRepository.findByVehicleTypeAndParkingLotIdAndActiveTrue(
                request.getVehicleType(), request.getParkingLotId())
                .ifPresent(rule -> {
                    throw new ParkingException("Pricing rule already exists for vehicle type: " + 
                            request.getVehicleType() + " in this parking lot");
                });

        PricingRule pricingRule = new PricingRule();
        pricingRule.setVehicleType(request.getVehicleType());
        pricingRule.setFreeHours(request.getFreeHours());
        pricingRule.setHourlyRate(request.getHourlyRate());
        pricingRule.setDailyMaxRate(request.getDailyMaxRate());
        pricingRule.setParkingLot(parkingLot);
        pricingRule.setActive(true);

        pricingRule = pricingRuleRepository.save(pricingRule);
        log.info("Pricing rule created successfully with ID: {}", pricingRule.getId());

        return pricingRule;
    }

    @Override
    @Transactional
    public PricingRule updatePricingRule(Long ruleId, PricingRuleRequest request) {
        log.info("Updating pricing rule with ID: {}", ruleId);

        PricingRule pricingRule = pricingRuleRepository.findById(ruleId)
                .orElseThrow(() -> new ParkingException("Pricing rule not found: " + ruleId));

        // Validate parking lot exists
        parkingLotRepository.findByIdAndActiveTrue(request.getParkingLotId())
                .orElseThrow(() -> new ParkingException("Parking lot not found: " + request.getParkingLotId()));

        pricingRule.setVehicleType(request.getVehicleType());
        pricingRule.setFreeHours(request.getFreeHours());
        pricingRule.setHourlyRate(request.getHourlyRate());
        pricingRule.setDailyMaxRate(request.getDailyMaxRate());

        pricingRule = pricingRuleRepository.save(pricingRule);
        log.info("Pricing rule updated successfully: {}", ruleId);

        return pricingRule;
    }

    @Override
    @Transactional
    public void deletePricingRule(Long ruleId) {
        log.info("Deleting pricing rule with ID: {}", ruleId);

        PricingRule pricingRule = pricingRuleRepository.findById(ruleId)
                .orElseThrow(() -> new ParkingException("Pricing rule not found: " + ruleId));

        pricingRule.setActive(false);
        pricingRuleRepository.save(pricingRule);

        log.info("Pricing rule deleted successfully: {}", ruleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PricingRule> getAllPricingRules(Long parkingLotId) {
        parkingLotRepository.findByIdAndActiveTrue(parkingLotId)
                .orElseThrow(() -> new ParkingException("Parking lot not found: " + parkingLotId));

        return pricingRuleRepository.findByParkingLotIdAndActiveTrue(parkingLotId);
    }

    @Override
    public List<TicketDTO>  getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(t -> new TicketDTO(
                        t.getId(),
                        t.getTicketNumber(),
                        t.getVehicle() != null ? t.getVehicle().getPlateNumber() : null,
                        t.getSlot() != null ? t.getSlot().getSlotNumber() : null,
                        t.getEntryTime(),
                        t.getExitTime(),
                        t.getStatus(),
                        t.getUser().getUsername(),
                        paymentRepository.findByTicketId(t.getId())
                                .map(Payment::getAmount)
                                .orElse(BigDecimal.ZERO)
                ))
                .toList();
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
