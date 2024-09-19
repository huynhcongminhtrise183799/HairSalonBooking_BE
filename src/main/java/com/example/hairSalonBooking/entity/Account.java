package com.example.hairSalonBooking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long AccountID;
    @Column(unique = true)
    String userName;
    String password;
    @Column(unique = true)
    String email;
    String fullName;
    LocalDate Dob;
    int gender;
    String phone;
    String image;
    String googleID;
    String googleName;
    boolean isDelete = false;
    long RoleID;
    long SalonID;
    long LevelID;

}
