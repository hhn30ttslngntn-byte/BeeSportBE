package com.example.sport_be.repository;

import com.example.sport_be.model.LichSuSuDungMaGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LichSuSuDungMaGiamGiaRepository extends JpaRepository<LichSuSuDungMaGiamGia, Integer> {

    List<LichSuSuDungMaGiamGia> findByHoaDon_Id(Integer idHoaDon);

}
