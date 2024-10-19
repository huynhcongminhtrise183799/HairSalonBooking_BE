package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Booking;
import com.example.hairSalonBooking.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
    Feedback findFeedbackByBookingBookingId(long id);
//    List<Booking> findFeedbackByBookingId(long id);
}
