package com.example.sport_be.repository;

import com.example.sport_be.model.DiaChiVanChuyen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaChiVanChuyenRepository extends JpaRepository<DiaChiVanChuyen, Integer> {

    List<DiaChiVanChuyen> findByNguoiDung_Id(Integer idNguoiDung);

}
