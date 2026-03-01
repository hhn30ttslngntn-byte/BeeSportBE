package com.example.sport_be.repository;

import com.example.sport_be.model.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {

    List<SanPhamChiTiet> findBySanPham_Id(Integer idSanPham);

}
