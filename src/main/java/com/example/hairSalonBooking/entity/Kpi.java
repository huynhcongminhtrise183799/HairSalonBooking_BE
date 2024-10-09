package com.example.hairSalonBooking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Kpi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long kipId;
    String yearAndMonth;
    double revenueGenerated;
    double performanceScore;
    @ManyToOne
    @JoinColumn(name = "level_id")
    Level level;
}