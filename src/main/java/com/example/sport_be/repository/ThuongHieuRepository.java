package com.example.sport_be.repository;

import com.example.sport_be.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Integer> {
    List<ThuongHieu> findByTrangThaiTrue();
    Optional<ThuongHieu> findByMaThuongHieu(String ma);
}
