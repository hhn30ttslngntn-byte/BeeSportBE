package com.example.sport_be.repository;

import com.example.sport_be.entity.SanPhamYeuThich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamYeuThichRepository extends JpaRepository<SanPhamYeuThich, Integer> {
    List<SanPhamYeuThich> findByNguoiDungId(Integer userId);
    List<SanPhamYeuThich> findByNguoiDungIdAndSanPhamId(Integer userId, Integer productId);
    boolean existsByNguoiDungIdAndSanPhamId(Integer userId, Integer productId);
    void deleteByNguoiDungIdAndSanPhamId(Integer userId, Integer productId);
}
