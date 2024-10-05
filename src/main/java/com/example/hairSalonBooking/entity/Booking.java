package com.example.hairSalonBooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long bookingId;
    LocalDate bookingDay;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    SalonBranch salonBranch;

    @ManyToOne
    @JoinColumn(name = "account_id")
    Account account;

    @ManyToOne
    @JoinColumn(name = "stylist_schedule_id")
    StylistSchedule stylistSchedule;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    Slot slot;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    Voucher voucher;

    int status;

    @ManyToMany
    @JoinTable(name = "booking_detail",
    joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")

    )
    @JsonIgnore
    Set<SalonService> services;

    @OneToOne(mappedBy = "booking")
    Feedback feedback;
}