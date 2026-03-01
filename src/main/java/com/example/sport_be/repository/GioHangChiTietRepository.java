package com.example.sport_be.repository;

import com.example.sport_be.model.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {

    List<GioHangChiTiet> findByGioHang_Id(Integer idGioHang);

}
