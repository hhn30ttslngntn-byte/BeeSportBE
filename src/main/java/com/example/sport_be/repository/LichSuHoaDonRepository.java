package com.example.sport_be.repository;

import com.example.sport_be.model.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Integer> {

    List<LichSuHoaDon> findByHoaDon_Id(Integer idHoaDon);

}
