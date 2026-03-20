package com.example.sport_be.repository;

import com.example.sport_be.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
    List<SanPham> findByTrangThaiTrue();
    List<SanPham> findByTenSanPhamContainingIgnoreCaseAndTrangThaiTrue(String ten);
    Optional<SanPham> findByMa(String ma);
}
