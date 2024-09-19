package com.example.hairSalonBooking.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
    long id;
    String userName;
    String password;
    String email;
    String fullName;
    LocalDate Dob;
}
