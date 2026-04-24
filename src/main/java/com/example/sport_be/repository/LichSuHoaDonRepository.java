package com.example.sport_be.repository;

import com.example.sport_be.entity.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Integer> {
    List<LichSuHoaDon> findByHoaDonIdOrderByThoiGianDesc(Integer hoaDonId);
}
