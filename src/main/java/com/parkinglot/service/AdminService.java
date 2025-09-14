package com.parkinglot.service;

import com.parkinglot.dto.ParkingSlotRequest;
import com.parkinglot.dto.PricingRuleRequest;
import com.parkinglot.dto.TicketDTO;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.PricingRule;
import com.parkinglot.entity.Ticket;
import com.parkinglot.entity.User;

import java.util.List;

public interface AdminService {
    ParkingSlot addParkingSlot(ParkingSlotRequest request);
    void removeParkingSlot(Long slotId);
    List<ParkingSlot> getAllSlots(Long parkingLotId);

    PricingRule createPricingRule(PricingRuleRequest request);
    PricingRule updatePricingRule(Long ruleId, PricingRuleRequest request);
    void deletePricingRule(Long ruleId);
    List<PricingRule> getAllPricingRules(Long parkingLotId);
    List<TicketDTO> getAllTickets();
    List<User> getAllUsers();
}
