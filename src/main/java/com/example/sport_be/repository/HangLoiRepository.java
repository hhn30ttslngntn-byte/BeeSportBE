package com.example.sport_be.repository;

import com.example.sport_be.entity.HangLoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HangLoiRepository extends JpaRepository<HangLoi, Integer> {
}
