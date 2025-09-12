package com.parkinglot.service.impl;

import com.parkinglot.entity.ParkingLot;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.repo.ParkingLotRepository;
import com.parkinglot.repo.ParkingSlotRepository;
import com.parkinglot.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingServiceImpl implements ParkingService {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Override
    public List<ParkingLot> findAllParkingLot() {
        return parkingLotRepository.findAll();
    }

    @Override
    public List<ParkingSlot> findAllParkingSlot() {
        return parkingSlotRepository.findAll();
    }

    @Override
    public ParkingSlot findBySlotNumber(String slotNumber) {
        return parkingSlotRepository.findBySlotNumber(slotNumber);
    }
}
