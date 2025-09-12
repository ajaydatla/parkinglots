package com.parkinglot.config;

import com.parkinglot.entity.ParkingLot;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.enums.SlotStatus;
import com.parkinglot.enums.VehicleType;
import com.parkinglot.repo.ParkingLotRepository;
import com.parkinglot.repo.ParkingSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Override
    public void run(String... args) throws Exception {
        ParkingLot pl = createParkingLot();
        createParkingSlot(pl);
    }

    private ParkingLot createParkingLot() {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName("Central Mall Parkinggg");
        parkingLot.setActive(true);

        parkingLot = parkingLotRepository.save(parkingLot);

        return parkingLot;
    }

    private void createParkingSlot(ParkingLot pl){

        for (int i = 0; i < 10; i++) {
            ParkingSlot ps = ParkingSlot.builder()
                    .slotNumber(VehicleType.TRUCK.name()+ String.valueOf(i))
                    .type(VehicleType.TRUCK)
                    .status(SlotStatus.AVAILABLE)
//                    .parkingLot(pl)
                    .version(1L)
                    .build();

            parkingSlotRepository.save(ps);

            ParkingSlot ps1 = ParkingSlot.builder()
                    .slotNumber(VehicleType.CAR.name()+ String.valueOf(i))
                    .type(VehicleType.CAR)
                    .status(SlotStatus.AVAILABLE)
//                    .parkingLot(pl)
                    .version(1L)
                    .build();

            parkingSlotRepository.save(ps1);

            ParkingSlot ps2 = ParkingSlot.builder()
                    .slotNumber(VehicleType.BIKE.name()+ String.valueOf(i))
                    .type(VehicleType.BIKE)
                    .status(SlotStatus.AVAILABLE)
//                    .parkingLot(pl)
                    .version(1L)
                    .build();

            parkingSlotRepository.save(ps2);

        }

    }
}
