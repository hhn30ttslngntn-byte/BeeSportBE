package com.example.sport_be.repository;

import com.example.sport_be.entity.PtThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PtThanhToanRepository extends JpaRepository<PtThanhToan, Integer> {
}
