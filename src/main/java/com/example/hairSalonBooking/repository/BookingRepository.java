package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.entity.Booking;
import com.example.hairSalonBooking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    @Query(value = "select b.* from booking b\n" +
            "            inner join stylist_schedule ssch\n" +
            "            on b.stylist_schedule_id = ssch.stylist_schedule_id\n" +
            "            where ssch.working_day = ?1 and ssch.account_id = ?2\n" +
            "            order by b.slot_id desc ",nativeQuery = true)
    List<Booking> getBookingsByStylistInDay(LocalDate date, long stylistId);

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
    Booking findBookingByBookingId(long bookingId);
}
