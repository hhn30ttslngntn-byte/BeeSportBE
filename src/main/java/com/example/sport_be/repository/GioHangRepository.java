package com.example.sport_be.repository;

import com.example.sport_be.model.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GioHangRepository extends JpaRepository<GioHang, Integer> {

    Optional<GioHang> findByNguoiDung_IdAndTrangThai(Integer idNguoiDung, String trangThai);

}
