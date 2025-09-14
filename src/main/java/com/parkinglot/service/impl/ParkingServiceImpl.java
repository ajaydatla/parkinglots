package com.parkinglot.service.impl;

import com.parkinglot.dto.*;
import com.parkinglot.entity.*;
import com.parkinglot.enums.*;
import com.parkinglot.exception.*;
import com.parkinglot.repository.*;
import com.parkinglot.service.ParkingService;
import com.parkinglot.strategy.SlotAllocationStrategy;
import com.parkinglot.util.TicketNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class ParkingServiceImpl implements ParkingService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final VehicleRepository vehicleRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final UserRepository userRepository;

    @Qualifier("nearestSlotStrategy")
    private final SlotAllocationStrategy slotAllocationStrategy;

    public ParkingServiceImpl(
            ParkingSlotRepository parkingSlotRepository,
            VehicleRepository vehicleRepository,
            TicketRepository ticketRepository,
            PaymentRepository paymentRepository,
            PricingRuleRepository pricingRuleRepository,
            ParkingLotRepository parkingLotRepository,
            @Qualifier("nearestSlotStrategy") SlotAllocationStrategy slotAllocationStrategy,
            UserRepository userRepository
    ){
        this.parkingSlotRepository = parkingSlotRepository;
        this.vehicleRepository = vehicleRepository;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
        this.pricingRuleRepository = pricingRuleRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.slotAllocationStrategy = slotAllocationStrategy;
        this.userRepository = userRepository;
    }



    @Override
    @Transactional
    public VehicleEntryResponse parkVehicle(VehicleEntryRequest request, User user) {
        log.info("Processing vehicle entry for plate number: {}", request.getPlateNumber());

        // Check if vehicle is already parked
        ticketRepository.findByVehiclePlateNumberAndStatus(request.getPlateNumber(), TicketStatus.ACTIVE)
                .ifPresent(ticket -> {
                    throw new VehicleAlreadyParkedException(
                            "Vehicle " + request.getPlateNumber() + " is already parked with ticket: " + ticket.getTicketNumber());
                });

        // Get or create vehicle
        Vehicle vehicle = getOrCreateVehicle(request, user);

        // Determine parking lot
        Long parkingLotId = request.getParkingLotId() != null ? request.getParkingLotId() : getDefaultParkingLotId();

        // Find and allocate slot
        List<ParkingSlot> availableSlots = parkingSlotRepository
                .findAvailableSlotsByTypeWithLock(SlotStatus.AVAILABLE, request.getVehicleType(), parkingLotId);

        ParkingSlot allocatedSlot = slotAllocationStrategy.allocateSlot(availableSlots, request.getVehicleType())
                .orElseThrow(() -> new SlotNotAvailableException(
                        "No available slots for vehicle type: " + request.getVehicleType()));

        // Update slot status
        allocatedSlot.setStatus(SlotStatus.OCCUPIED);
        parkingSlotRepository.save(allocatedSlot);

        // Generate ticket
        Ticket ticket = createTicket(vehicle, allocatedSlot);
        ticket = ticketRepository.save(ticket);

        log.info("Vehicle parked successfully. Ticket: {}", ticket.getTicketNumber());

        return VehicleEntryResponse.builder()
                .ticketNumber(ticket.getTicketNumber())
                .plateNumber(vehicle.getPlateNumber())
                .vehicleType(vehicle.getVehicleType())
                .slotNumber(allocatedSlot.getSlotNumber())
                .floorNumber(allocatedSlot.getFloorNumber())
                .entryTime(ticket.getEntryTime())
                .message("Vehicle parked successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleExitResponse calculateExitFee(VehicleExitRequest request, User user) {
        log.info("Calculating exit fee for ticket: {}", request.getTicketNumber());

        Ticket ticket = ticketRepository.findByTicketNumber(request.getTicketNumber())
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found: " + request.getTicketNumber()));

        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            throw new ParkingException("Ticket is not active: " + request.getTicketNumber());
        }

        // Verify ownership
        if (!ticket.getVehicle().getOwner().getId().equals(user.getId()) && user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedAccessException("You are not authorized to access this ticket");
        }

        LocalDateTime exitTime = LocalDateTime.now();
        BigDecimal totalAmount = calculateParkingFee(ticket, exitTime);
        long totalHours = Duration.between(ticket.getEntryTime(), exitTime).toHours();

        return VehicleExitResponse.builder()
                .ticketNumber(ticket.getTicketNumber())
                .plateNumber(ticket.getVehicle().getPlateNumber())
                .totalAmount(totalAmount)
                .entryTime(ticket.getEntryTime())
                .exitTime(exitTime)
                .totalHours(totalHours)
                .message("Exit fee calculated successfully")
                .build();
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, User user) {
        log.info("Processing payment for ticket: {}", request.getTicketNumber());

        Ticket ticket = ticketRepository.findByTicketNumber(request.getTicketNumber())
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found: " + request.getTicketNumber()));

        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            throw new ParkingException("Ticket is not active: " + request.getTicketNumber());
        }

        // Verify ownership
        if (!ticket.getVehicle().getOwner().getId().equals(user.getId()) && user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedAccessException("You are not authorized to pay for this ticket");
        }

        // Check if payment already exists
        paymentRepository.findByTicketId(ticket.getId()).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                throw new PaymentFailedException("Payment already completed for this ticket");
            }
        });

        LocalDateTime exitTime = LocalDateTime.now();
        BigDecimal amount = calculateParkingFee(ticket, exitTime);

        // Create payment record
        Payment payment = new Payment();
        payment.setTicket(ticket);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.COMPLETED); // Simulating successful payment
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionId(generateTransactionId());
        payment = paymentRepository.save(payment);

        // Update ticket
        ticket.setExitTime(exitTime);
        ticket.setStatus(TicketStatus.PAID);
        ticketRepository.save(ticket);

        // Free the parking slot
        ParkingSlot slot = ticket.getSlot();
        slot.setStatus(SlotStatus.AVAILABLE);
        parkingSlotRepository.save(slot);

        log.info("Payment processed successfully for ticket: {}", ticket.getTicketNumber());

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .ticketNumber(ticket.getTicketNumber())
                .amount(amount)
                .status(PaymentStatus.COMPLETED)
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .paymentTime(payment.getCreatedAt())
                .message("Payment processed successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingStatusResponse getParkingStatus(Long parkingLotId) {
        ParkingLot parkingLot = parkingLotRepository.findByIdAndActiveTrue(parkingLotId)
                .orElseThrow(() -> new ParkingException("Parking lot not found: " + parkingLotId));

        List<ParkingSlot> allSlots = parkingSlotRepository.findByParkingLotId(parkingLotId);

        Map<String, Integer> availableSlots = new HashMap<>();
        Map<String, Integer> occupiedSlots = new HashMap<>();
        Map<String, Integer> totalSlots = new HashMap<>();

        // Initialize maps
        for (VehicleType type : VehicleType.values()) {
            availableSlots.put(type.name(), 0);
            occupiedSlots.put(type.name(), 0);
            totalSlots.put(type.name(), 0);
        }

        // Count slots by type and status
        for (ParkingSlot slot : allSlots) {
            String vehicleType = slot.getVehicleType().name();
            totalSlots.put(vehicleType, totalSlots.get(vehicleType) + 1);

            if (slot.getStatus() == SlotStatus.AVAILABLE) {
                availableSlots.put(vehicleType, availableSlots.get(vehicleType) + 1);
            } else if (slot.getStatus() == SlotStatus.OCCUPIED) {
                occupiedSlots.put(vehicleType, occupiedSlots.get(vehicleType) + 1);
            }
        }

        return ParkingStatusResponse.builder()
                .parkingLotId(parkingLot.getId())
                .parkingLotName(parkingLot.getName())
                .availableSlots(availableSlots)
                .occupiedSlots(occupiedSlots)
                .totalSlots(totalSlots)
                .totalFloors(parkingLot.getTotalFloors())
                .build();
    }

    @Override
    public Optional<User> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    private Vehicle getOrCreateVehicle(VehicleEntryRequest request, User user) {
        return vehicleRepository.findByPlateNumber(request.getPlateNumber())
                .orElseGet(() -> {
                    Vehicle vehicle = new Vehicle();
                    vehicle.setPlateNumber(request.getPlateNumber());
                    vehicle.setVehicleType(request.getVehicleType());
                    vehicle.setOwner(user);
                    return vehicleRepository.save(vehicle);
                });
    }

    private Ticket createTicket(Vehicle vehicle, ParkingSlot slot) {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(TicketNumberGenerator.generate());
        ticket.setVehicle(vehicle);
        ticket.setSlot(slot);
        ticket.setEntryTime(LocalDateTime.now());
        ticket.setStatus(TicketStatus.ACTIVE);
        return ticket;
    }

    private BigDecimal calculateParkingFee(Ticket ticket, LocalDateTime exitTime) {
        PricingRule pricingRule = pricingRuleRepository
                .findByVehicleTypeAndParkingLotIdAndActiveTrue(
                        ticket.getVehicle().getVehicleType(),
                        ticket.getSlot().getParkingLot().getId())
                .orElseThrow(() -> new ParkingException("Pricing rule not found for vehicle type: " +
                        ticket.getVehicle().getVehicleType()));

        long totalHours = Duration.between(ticket.getEntryTime(), exitTime).toHours();
        if (totalHours == 0) {
            totalHours = 1; // Minimum 1 hour charging
        }

        long chargeableHours = Math.max(0, totalHours - pricingRule.getFreeHours());
        BigDecimal totalAmount = pricingRule.getHourlyRate().multiply(BigDecimal.valueOf(chargeableHours));

        // Apply daily maximum if applicable
        if (pricingRule.getDailyMaxRate() != null && totalAmount.compareTo(pricingRule.getDailyMaxRate()) > 0) {
            totalAmount = pricingRule.getDailyMaxRate();
        }

        return totalAmount;
    }

    private Long getDefaultParkingLotId() {
        return parkingLotRepository.findByActiveTrue().stream()
                .findFirst()
                .map(ParkingLot::getId)
                .orElseThrow(() -> new ParkingException("No active parking lot found"));
    }

    private String generateTransactionId() {
        return "TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
