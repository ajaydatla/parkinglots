package com.parkinglot.repository;

import com.parkinglot.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByPlateNumber(String plateNumber);
    List<Vehicle> findByOwnerId(Long ownerId);
    boolean existsByPlateNumber(String plateNumber);
}
