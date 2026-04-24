package com.example.sport_be.repository;

import com.example.sport_be.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    List<HoaDon> findByNguoiDungIdOrderByNgayTaoDesc(Integer nguoiDungId);
    List<HoaDon> findByTrangThaiDon(String trangThaiDon);
    List<HoaDon> findByTrangThaiDonAndNgayGiaoNotNull(String trangThaiDon);
    Optional<HoaDon> findByMaHoaDon(String maHoaDon);
}
