package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelRepository extends JpaRepository<Level,Long> {
    Level findLevelByLevelname(String name);
}
