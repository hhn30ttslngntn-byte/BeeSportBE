package com.example.sport_be.repository;

import com.example.sport_be.entity.CauHinhDoiTra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CauHinhDoiTraRepository extends JpaRepository<CauHinhDoiTra, Integer> {
    Optional<CauHinhDoiTra> findTopByOrderByIdAsc();
}
