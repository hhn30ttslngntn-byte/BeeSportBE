package com.example.sport_be.repository;

import com.example.sport_be.model.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorRepository extends JpaRepository<MauSac, Integer> {

    boolean existsByTenMau(String tenMau);
}
