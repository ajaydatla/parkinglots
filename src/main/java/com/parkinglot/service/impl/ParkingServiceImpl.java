package com.parkinglot.service.impl;

import com.parkinglot.entity.ParkingLot;
import com.parkinglot.repo.ParkingLotRepository;
import com.parkinglot.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingServiceImpl implements ParkingService {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Override
    public List<ParkingLot> findAl() {
        return parkingLotRepository.findAll();
    }
}
