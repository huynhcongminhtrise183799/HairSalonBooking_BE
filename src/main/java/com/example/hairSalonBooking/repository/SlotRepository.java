package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    Slot findSlotBySlotid(Long slotid);
}
