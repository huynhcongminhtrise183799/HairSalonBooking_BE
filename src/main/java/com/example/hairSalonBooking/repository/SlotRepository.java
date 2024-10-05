package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    Slot findSlotBySlotid(Long slotid);
    @Query(value = "select * from slot where slot.slottime > ?1\n" +
            "order by slottime asc",nativeQuery = true)
    List<Slot> getAllSlotCanBooking(LocalTime time);

    @Query(value = "select * from slot",nativeQuery = true)
    Set<Slot> getAllSlot();

    @Query(value = "select * from slot where slot.slottime > ?1\n" +
            "order by slottime asc\n" +
            "limit ?2 ",nativeQuery = true)
    List<Slot> getSlotToRemove(LocalTime time, int limit);
}
