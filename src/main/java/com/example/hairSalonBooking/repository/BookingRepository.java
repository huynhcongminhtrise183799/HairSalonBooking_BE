package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.entity.Booking;
import com.example.hairSalonBooking.entity.SalonBranch;
import com.example.hairSalonBooking.enums.BookingStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    @Query(value = "select b.* from booking b\n" +
            "            inner join stylist_schedule ssch\n" +
            "            on b.stylist_schedule_id = ssch.stylist_schedule_id\n" +
            "            where ssch.working_day = ?1 and ssch.account_id = ?2 and b.status != 'CANCELLED'\n" +
            "            order by b.slot_id desc ",nativeQuery = true)
    List<Booking> getBookingsByStylistInDay(LocalDate date, long stylistId);

    @Query(value = "select b.* from booking b\n" +
            "      inner join stylist_schedule ssch\n" +
            "         on b.stylist_schedule_id = ssch.stylist_schedule_id\n" +
            "        where ssch.working_day = ?1 and ssch.account_id = ?2 and b.status != 'CANCELLED' and b.booking_id != ?3\n" +
            "          order by b.slot_id desc", nativeQuery = true)
    List<Booking> getBookingsByStylistInDayForUpdate(LocalDate date, long stylistId, long bookingId);



    @Query(value = "select b.* from booking b\n" +
            "inner join slot sl\n" +
            "on b.slot_id = sl.slotid\n" +
            "inner join stylist_schedule ss\n" +
            "on b.stylist_schedule_id = ss.stylist_schedule_id\n" +
            "where ss.account_id = ?1 and sl.slottime > ?2 and b.booking_day = ?3\n" +
            "and b.status != 'CANCELLED'\n" +
            "limit 1",nativeQuery = true)
    Booking bookingNearestOverTime(long stylistId, LocalTime time, LocalDate date);
    @Query(value = "select b.* from booking b\n" +
            "inner join slot sl\n" +
            "on b.slot_id = sl.slotid\n" +
            "inner join stylist_schedule ss\n" +
            "on b.stylist_schedule_id = ss.stylist_schedule_id\n" +
            "where ss.account_id = ?1 and sl.slottime < ?2 and b.booking_day = ?3\n" +
            "and b.status != 'CANCELLED'\n" +
            "limit 1",nativeQuery = true)
    Booking bookingNearestBeforeTime(long stylistId, LocalTime time, LocalDate date);
    @Query(value = "select b.* from booking b\n" +
            "inner join slot s \n" +
            "on b.slot_id = s.slotid\n" +
            "inner join stylist_schedule ss\n" +
            "on b.stylist_schedule_id = ss.stylist_schedule_id\n" +
            "where s.slotid = ?1 and ss.account_id = ?2 and ss.working_day = ?3 and b.status != 'CANCELLED'",nativeQuery = true)
    Booking bookingAtTime(long slotId, long id, LocalDate date);







    Booking findBookingByBookingId(long id);
    @Query(value = "select b.* from booking b \n" +
            "inner join stylist_schedule ssch\n" +
            "on b.stylist_schedule_id = ssch.stylist_schedule_id\n" +
            "inner join booking_detail bd\n" +
            "on b.booking_id = bd.booking_id\n" +
            "where ssch.working_day = ?1 and ssch.account_id = ?2\n" +
            "order by b.booking_id desc\n" +
            "limit 1 ",nativeQuery = true)
    Booking bookingNearest(LocalDate date, long stylistId);

    List<Booking> findByAccountAndStatus(Account account, BookingStatus status);



    @Query(value = "select * from booking b\n" +
            "where b.account_id = ?1 and b.status = ?2;",nativeQuery = true)
    List<Booking> getBookingsByIdAndSatus(long id, String status);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM booking_detail WHERE booking_id = ?1 ",nativeQuery = true)
    void deleteBookingDetail(long id);
    @Query("SELECT b FROM Booking b WHERE b.account.accountid = :stylistId AND b.account.role = 'STYLIST' AND b.bookingDay = :today")
    List<Booking> findBookingsByStylistAndDate(@Param("stylistId") Long stylistId, @Param("today") LocalDate today);

    @Query(value = "select b.* from booking b\n" +
            "inner join stylist_schedule ss\n" +
            "on b.stylist_schedule_id = ss.stylist_schedule_id\n" +
            "where ss.account_id = ?1 and b.booking_day = ?2",nativeQuery = true)
    List<Booking> findAllByAccountInAndSalonBranch(long stylistId, LocalDate date);

    @Query(value = "select count(*) from booking b\n" +
            "inner join slot s\n" +
            "on b.slot_id = s.slotid\n" +
            "inner join specific_stylist_schedule ssch\n" +
            "on b.stylist_schedule_id = ssch.stylist_schedule_id\n" +
            "inner join stylist_schedule ss\n" +
            "on b.stylist_schedule_id = ss.stylist_schedule_id\n" +
            "inner join shift sh\n" +
            "on ssch.shift_id = sh.shift_id\n" +
            "where s.slottime >= sh.start_time and s.slottime < sh.end_time and  sh.shift_id = ?1 and b.status = 'COMPLETED' \n" +
            "and ss.account_id = ?2 and ss.working_day = ?3;",nativeQuery = true)
    int countTotalBookingCompleteInShift(long shiftId, long accountId, LocalDate date);

    @Query(value = "select b.* from booking b where b.booking_day = ?1 and b.status = 'PENDING'",nativeQuery = true)
    List<Booking> getBookingByDateAndStatusPending(LocalDate date);
}
