package com.example.sport_be.repository;

import com.example.sport_be.model.HinhAnhSanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HinhAnhSanPhamRepository extends JpaRepository<HinhAnhSanPham, Integer> {

    List<HinhAnhSanPham> findBySanPham_Id(Integer idSanPham);

}
