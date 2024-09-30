package com.example.hairSalonBooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class SalonService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long serviceId;
    @Column(nullable = false)
    String serviceName;
    @Column(nullable = false)
    int price;
    String description;
    LocalTime duration;
    @ManyToOne
    @JoinColumn(name = "skill_id")
    Skill skill;

}
