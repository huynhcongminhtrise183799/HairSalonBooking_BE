package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Skill findSkillBySkillName(String name);
    Skill findSkillBySkillId(long id);
}
