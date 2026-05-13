package com.example.sport_be.repository;

import com.example.sport_be.entity.DoiTra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoiTraRepository extends JpaRepository<DoiTra, Integer> {
    List<DoiTra> findByHoaDonId(Integer hoaDonId);

    Optional<DoiTra> findByTokenXacNhan(String tokenXacNhan);
}
