package com.example.hairSalonBooking.model.response;

import com.example.hairSalonBooking.entity.SalonService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {

    String customerName;

    String salonName;
    String stylistName;
    LocalDate date;
    LocalTime time;

    String voucherCode;

    Set<String> serviceName;
}
