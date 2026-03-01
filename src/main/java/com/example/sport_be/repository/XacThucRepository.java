package com.example.sport_be.repository;

import com.example.sport_be.model.XacThuc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XacThucRepository extends JpaRepository<XacThuc, Integer> {

    List<XacThuc> findByNguoiDung_Id(Integer idNguoiDung);

}
