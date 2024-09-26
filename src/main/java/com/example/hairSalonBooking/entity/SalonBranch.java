package com.example.hairSalonBooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class SalonBranch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long salonid;
    String address;
    @Column(unique = true)
    String hotline;
    boolean isDelete = false;
    @OneToMany(mappedBy = "salonBranch")
    @JsonIgnore
    List<Account> accounts;

}
