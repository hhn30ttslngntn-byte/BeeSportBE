package com.example.sport_be.repository;

import com.example.sport_be.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {
    Optional<GioHang> findByNguoiDungIdAndTrangThai(Integer nguoiDungId, String trangThai);
}
