package com.parkinglot.repository;

import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.enums.SlotStatus;
import com.parkinglot.enums.VehicleType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.status = :status AND ps.vehicleType = :vehicleType AND ps.parkingLot.id = :parkingLotId ORDER BY ps.floorNumber ASC, ps.slotNumber ASC")
    List<ParkingSlot> findAvailableSlotsByTypeWithLock(@Param("status") SlotStatus status, 
                                                       @Param("vehicleType") VehicleType vehicleType,
                                                       @Param("parkingLotId") Long parkingLotId);

    @Query("SELECT COUNT(ps) FROM ParkingSlot ps WHERE ps.status = :status AND ps.vehicleType = :vehicleType AND ps.parkingLot.id = :parkingLotId")
    long countByStatusAndVehicleTypeAndParkingLotId(@Param("status") SlotStatus status, 
                                                    @Param("vehicleType") VehicleType vehicleType,
                                                    @Param("parkingLotId") Long parkingLotId);

    List<ParkingSlot> findByParkingLotIdAndFloorNumber(Long parkingLotId, Integer floorNumber);

    List<ParkingSlot> findByParkingLotId(Long parkingLotId);

    Optional<ParkingSlot> findBySlotNumberAndParkingLotId(String slotNumber, Long parkingLotId);
}
