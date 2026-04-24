package com.example.sport_be.repository;

import com.example.sport_be.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {
    List<GioHang> findByNguoiDungIdAndTrangThaiAndLoaiGioHang(Integer nguoiDungId, String trangThai, String loai);
    List<GioHang> findByNguoiDungIdAndTrangThai(Integer nguoiDungId, String trangThai);
    List<GioHang> findByLoaiGioHangAndTrangThai(String loai, String trangThai);
}
