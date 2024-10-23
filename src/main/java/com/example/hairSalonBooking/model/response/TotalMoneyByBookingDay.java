package com.example.hairSalonBooking.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.checkerframework.checker.units.qual.N;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TotalMoneyByBookingDay {
    LocalDate day;
    double totalMoney;
}
