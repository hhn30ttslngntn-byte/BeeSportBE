package com.example.sport_be.repository;

import com.example.sport_be.entity.GiamGiaSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiamGiaSanPhamRepository extends JpaRepository<GiamGiaSanPham, Integer> {
    List<GiamGiaSanPham> findByDotGiamGiaId(Integer dotGiamGiaId);
    void deleteByDotGiamGiaId(Integer dotGiamGiaId);
}
