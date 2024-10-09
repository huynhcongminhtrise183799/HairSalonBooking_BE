package com.example.hairSalonBooking.model.response;

import com.example.hairSalonBooking.entity.SalonService;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {
    String salonName;
    String stylistName;
    LocalDate date;
    LocalTime time;
    Set<SalonService> serviceName;
}
