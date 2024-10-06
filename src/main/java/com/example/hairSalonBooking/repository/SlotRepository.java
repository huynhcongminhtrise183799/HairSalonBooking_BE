package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    Slot findSlotBySlotid(Long slotid);
    @Query(value = "select * from slot where slot.slottime >= ?1\n" +
            "order by slottime asc",nativeQuery = true)
    List<Slot> getAllSlotCanBooking(LocalTime time);

    @Query(value = "select * from slot",nativeQuery = true)
    Set<Slot> getAllSlot();

    @Query(value = "select * from slot where slot.slottime > ?1\n" +
            "order by slottime asc\n" +
            "limit ?2 ",nativeQuery = true)
    List<Slot> getSlotToRemove(LocalTime time, int limit);
    @Query(value = "SELECT DISTINCT s.*\n" +
            "FROM slot s\n" +
            "JOIN specific_stylist_schedule sss ON sss.shift_id = ?1  -- Thay số cụ thể ở đây\n" +
            "JOIN shift sh ON sss.shift_id = sh.shift_id\n" +
            "WHERE s.slottime >= sh.start_time AND s.slottime < sh.end_time;",nativeQuery = true)
    List<Slot> getSlotsInShift(long id);
}
