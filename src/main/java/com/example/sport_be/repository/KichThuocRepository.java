package com.example.sport_be.repository;

import com.example.sport_be.entity.KichThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KichThuocRepository extends JpaRepository<KichThuoc, Integer> {
    Optional<KichThuoc> findByTen(String ten);
    Optional<KichThuoc> findByMa(String ma);
}
