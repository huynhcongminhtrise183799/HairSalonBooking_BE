package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
    Feedback findFeedbackByBookingBookingId(long id);
}
