package com.example.sport_be.repository;

import com.example.sport_be.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, Integer> {
    List<VaiTro> findByTen(String ten);
    List<VaiTro> findByMa(String ma);
}
