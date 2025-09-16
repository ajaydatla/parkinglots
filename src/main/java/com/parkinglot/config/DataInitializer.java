package com.parkinglot.config;

import com.parkinglot.entity.ParkingLot;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.PricingRule;
import com.parkinglot.entity.User;
import com.parkinglot.enums.SlotStatus;
import com.parkinglot.enums.UserRole;
import com.parkinglot.enums.VehicleType;
import com.parkinglot.repository.ParkingLotRepository;
import com.parkinglot.repository.ParkingSlotRepository;
import com.parkinglot.repository.PricingRuleRepository;
import com.parkinglot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private PricingRuleRepository pricingRuleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing sample data...");

        // Check if data already exists
        if (parkingLotRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        // Create sample admin user
        createAdminUser();
        createAUser();

        // Create parking lot
        ParkingLot parkingLot = createParkingLot();

        // Create parking slots
        createParkingSlots(parkingLot);

        // Create pricing rules
        createPricingRules(parkingLot);

        log.info("Sample data initialization completed");
    }

    private void createAdminUser() {
        User admin = new User();
        admin.setUsername("admin@parkinglot.com");
        admin.setRole(UserRole.ADMIN);

        userRepository.save(admin);
        log.info("Admin user created: {}", admin.getUsername());
    }

    private void createAUser() {
        User user = new User();
        user.setUsername("user@parkinglot.com");
        user.setRole(UserRole.USER);

        userRepository.save(user);
        log.info("user user created: {}", user.getUsername());
    }

    private ParkingLot createParkingLot() {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName("Central Mall Parking");
        parkingLot.setLocation("Downtown Business District");
        parkingLot.setTotalFloors(3);
        parkingLot.setActive(true);

        parkingLot = parkingLotRepository.save(parkingLot);
        log.info("Parking lot created: {}", parkingLot.getName());

        return parkingLot;
    }

    private void createParkingSlots(ParkingLot parkingLot) {
        // Floor 1: Ground floor - Mixed vehicles
        createSlotsForFloor(parkingLot, 1, "A", 5, VehicleType.BIKE);
        createSlotsForFloor(parkingLot, 1, "B", 10, VehicleType.CAR);
        createSlotsForFloor(parkingLot, 1, "C", 3, VehicleType.TRUCK);

        // Floor 2: First floor - Cars and bikes
        createSlotsForFloor(parkingLot, 2, "A", 8, VehicleType.BIKE);
        createSlotsForFloor(parkingLot, 2, "B", 15, VehicleType.CAR);

        // Floor 3: Second floor - Cars only
        createSlotsForFloor(parkingLot, 3, "A", 20, VehicleType.CAR);

        log.info("Created parking slots for all floors");
    }

    private void createSlotsForFloor(ParkingLot parkingLot, int floor, String section, int count, VehicleType vehicleType) {
        for (int i = 1; i <= count; i++) {
            ParkingSlot slot = new ParkingSlot();
            slot.setSlotNumber(String.format("%s%02d", section, i));
            slot.setFloorNumber(floor);
            slot.setVehicleType(vehicleType);
            slot.setStatus(SlotStatus.AVAILABLE);
            slot.setParkingLot(parkingLot);

            parkingSlotRepository.save(slot);
        }
    }

    private void createPricingRules(ParkingLot parkingLot) {
        // Bike pricing: First 2 hours free, then ₹10/hour
        PricingRule bikeRule = new PricingRule();
        bikeRule.setVehicleType(VehicleType.BIKE);
        bikeRule.setFreeHours(0);
        bikeRule.setHourlyRate(BigDecimal.valueOf(10.00));
        bikeRule.setDailyMaxRate(BigDecimal.valueOf(100.00));
        bikeRule.setParkingLot(parkingLot);
        bikeRule.setActive(true);

        // Car pricing: First 2 hours free, then ₹20/hour
        PricingRule carRule = new PricingRule();
        carRule.setVehicleType(VehicleType.CAR);
        carRule.setFreeHours(0);
        carRule.setHourlyRate(BigDecimal.valueOf(20.00));
        carRule.setDailyMaxRate(BigDecimal.valueOf(300.00));
        carRule.setParkingLot(parkingLot);
        carRule.setActive(true);

        // Truck pricing: First 1 hour free, then ₹50/hour
        PricingRule truckRule = new PricingRule();
        truckRule.setVehicleType(VehicleType.TRUCK);
        truckRule.setFreeHours(0);
        truckRule.setHourlyRate(BigDecimal.valueOf(50.00));
        truckRule.setDailyMaxRate(BigDecimal.valueOf(500.00));
        truckRule.setParkingLot(parkingLot);
        truckRule.setActive(true);

        pricingRuleRepository.save(bikeRule);
        pricingRuleRepository.save(carRule);
        pricingRuleRepository.save(truckRule);

        log.info("Pricing rules created for all vehicle types");
    }
}
