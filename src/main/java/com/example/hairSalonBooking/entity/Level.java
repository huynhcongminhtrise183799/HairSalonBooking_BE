package com.example.hairSalonBooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long levelid;
    String levelname;
    double salary;
    double bonussalary;
    @OneToMany(mappedBy = "level")
    @JsonIgnore
    List<Account> accounts;
    @OneToMany(mappedBy = "level")
    @JsonIgnore
    List<Kpi> kpis;
}
