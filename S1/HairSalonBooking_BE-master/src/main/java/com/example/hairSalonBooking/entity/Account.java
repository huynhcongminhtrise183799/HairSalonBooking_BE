package com.example.hairSalonBooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
   /* @Id
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
*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long accountid;
    @Column(unique = true)
    @NotBlank(message = "Code can not be blank!")
    String username;
    @Size(min = 6, message = "Password must be at least 6 character!")
    String password;
    @Column(unique = true)
    @Email(message = "Invalid email")
    String email;
    String fullname;
    LocalDate dob;
    int gender;
    @Pattern(regexp = "(84|0[3|5|7|8|9])+(\\d{8})\\b")
    @Column(unique = true)
    String phone;
    String image;
    String googleid;
    String googlename;
    boolean isDelete = false;

    long roleid;
    @Column(nullable = true)
    Long salonid;
    @Column(nullable = true)
    Long levelid;
}
