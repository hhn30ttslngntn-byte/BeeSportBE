package com.example.sport_be.repository;

import com.example.sport_be.entity.LichSuDoiTra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LichSuDoiTraRepository extends JpaRepository<LichSuDoiTra, Integer> {
    List<LichSuDoiTra> findByHoaDonIdOrderByNgayTaoDesc(Integer hoaDonId);
    List<LichSuDoiTra> findByDoiTraIdOrderByNgayTaoDesc(Integer doiTraId);
}
