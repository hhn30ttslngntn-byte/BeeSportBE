package com.example.sport_be.repository;

import com.example.sport_be.model.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    List<HoaDon> findByNguoiDung_Id(Integer idNguoiDung);

}
