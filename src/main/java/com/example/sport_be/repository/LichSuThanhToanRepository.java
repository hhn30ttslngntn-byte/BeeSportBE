package com.example.sport_be.repository;

import com.example.sport_be.entity.LichSuThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LichSuThanhToanRepository extends JpaRepository<LichSuThanhToan, Integer> {
}
