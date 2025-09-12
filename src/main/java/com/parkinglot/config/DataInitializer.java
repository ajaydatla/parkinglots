package com.parkinglot.config;

import com.parkinglot.entity.ParkingLot;
import com.parkinglot.repo.ParkingLotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Override
    public void run(String... args) throws Exception {
        createParkingLot();
    }

    private ParkingLot createParkingLot() {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName("Central Mall Parkinggg");
        parkingLot.setActive(true);

        parkingLot = parkingLotRepository.save(parkingLot);

        return parkingLot;
    }
}
