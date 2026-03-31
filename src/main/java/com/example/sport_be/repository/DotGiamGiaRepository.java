package com.example.sport_be.repository;

import com.example.sport_be.entity.DotGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DotGiamGiaRepository extends JpaRepository<DotGiamGia, Integer> {
}
