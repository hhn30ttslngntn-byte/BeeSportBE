package com.example.sport_be.repository;

import com.example.sport_be.model.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    List<SanPham> findByDanhMuc_Id(Integer idDanhMuc);

    List<SanPham> findByTrangThai(Boolean trangThai);

}
