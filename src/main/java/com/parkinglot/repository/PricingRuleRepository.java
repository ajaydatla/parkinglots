package com.parkinglot.repository;

import com.parkinglot.entity.PricingRule;
import com.parkinglot.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    Optional<PricingRule> findByVehicleTypeAndParkingLotIdAndActiveTrue(VehicleType vehicleType, Long parkingLotId);
    List<PricingRule> findByParkingLotIdAndActiveTrue(Long parkingLotId);
    List<PricingRule> findByActiveTrue();
}
