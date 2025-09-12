package com.parkinglot.service;

import com.parkinglot.entity.ParkingLot;
import com.parkinglot.entity.ParkingSlot;

import java.util.List;

public interface ParkingService {

    List<ParkingLot> findAllParkingLot();
    List<ParkingSlot> findAllParkingSlot();
    ParkingSlot findBySlotNumber(String slotNumber);

}
