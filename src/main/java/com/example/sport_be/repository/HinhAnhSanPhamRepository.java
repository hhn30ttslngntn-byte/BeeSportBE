package com.example.sport_be.repository;

import com.example.sport_be.entity.HinhAnhSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhAnhSanPhamRepository extends JpaRepository<HinhAnhSanPham, Integer> {
    List<HinhAnhSanPham> findBySanPhamId(Integer sanPhamId);
}
