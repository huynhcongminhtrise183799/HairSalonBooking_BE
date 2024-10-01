package com.example.hairSalonBooking.repository;


import com.example.hairSalonBooking.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByName(String name);
    boolean existsByName(String name);

}
