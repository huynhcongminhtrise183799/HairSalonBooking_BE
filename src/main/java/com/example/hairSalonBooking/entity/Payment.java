package com.example.hairSalonBooking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long paymentId;
    double paymentAmount;
    LocalDate paymentDate;
    String paymentMethod;
    String paymentStatus;
    String transactionId;
    @OneToOne
    @JoinColumn(name = "booking_id")
    Booking booking;
}