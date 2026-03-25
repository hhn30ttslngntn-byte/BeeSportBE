package com.example.sport_be.repository;

import com.example.sport_be.entity.Huyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HuyenRepository extends JpaRepository<Huyen, Integer> {
    List<Huyen> findByTinhIdAndTrangThaiTrueOrderByTenHuyenAsc(Integer tinhId);
}
