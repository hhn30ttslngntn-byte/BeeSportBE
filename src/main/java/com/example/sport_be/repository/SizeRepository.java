package com.example.sport_be.repository;

import com.example.sport_be.model.KichThuoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<KichThuoc, Integer> {

    boolean existsByTenKichThuoc(String tenKichThuoc);
    boolean existsByMaKichThuoc(String maKichThuoc);
}
