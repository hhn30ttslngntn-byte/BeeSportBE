package com.example.sport_be.repository;

import com.example.sport_be.model.MaGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia, Integer> {

    Optional<MaGiamGia> findByMaCode(String maCode);

}
