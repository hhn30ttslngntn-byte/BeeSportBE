package com.example.sport_be.repository;

import com.example.sport_be.entity.Tinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TinhRepository extends JpaRepository<Tinh, Integer> {
    List<Tinh> findByTrangThaiTrueOrderByTenTinhAsc();
    Optional<Tinh> findByTenTinh(String tenTinh);
}
