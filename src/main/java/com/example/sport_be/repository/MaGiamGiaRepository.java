package com.example.sport_be.repository;

import com.example.sport_be.entity.MaGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia, Integer> {
    Optional<MaGiamGia> findByMaCode(String maCode);
    List<MaGiamGia> findByTrangThaiTrue();
}
