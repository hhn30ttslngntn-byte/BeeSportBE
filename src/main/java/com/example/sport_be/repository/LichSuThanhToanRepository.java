package com.example.sport_be.repository;

import com.example.sport_be.model.LichSuThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LichSuThanhToanRepository extends JpaRepository<LichSuThanhToan, Integer> {

    List<LichSuThanhToan> findByHoaDon_Id(Integer idHoaDon);

}
