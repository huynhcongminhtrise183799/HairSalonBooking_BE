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
    long accountid;
    @Column(unique = true)
    String username;
    String password;
    @Column(unique = true)
    String email;
    String fullname;
    LocalDate dob;
    int gender;
    String phone;
    String image;
    String googleID;
    String googleName;
    boolean isDelete = false;

    long RoleID;
    @Column(nullable = true)
    Long SalonID;
    @Column(nullable = true)
    Long LevelID;

}
