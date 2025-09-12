package com.parkinglot.repository;

import com.parkinglot.entity.Ticket;
import com.parkinglot.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);

    @Query("SELECT t FROM Ticket t WHERE t.vehicle.plateNumber = :plateNumber AND t.status = :status")
    Optional<Ticket> findByVehiclePlateNumberAndStatus(@Param("plateNumber") String plateNumber, 
                                                       @Param("status") TicketStatus status);

    List<Ticket> findByVehicleOwnerIdAndStatus(Long ownerId, TicketStatus status);

    @Query("SELECT t FROM Ticket t WHERE t.entryTime BETWEEN :startTime AND :endTime")
    List<Ticket> findByEntryTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime);
}
