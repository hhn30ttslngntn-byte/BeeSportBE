package com.example.sport_be.repository;

import com.example.sport_be.model.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {

    List<HoaDonChiTiet> findByHoaDon_Id(Integer idHoaDon);

}
