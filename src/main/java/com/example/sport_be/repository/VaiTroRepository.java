package com.example.sport_be.repository;

import com.example.sport_be.model.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VaiTroRepository extends JpaRepository<VaiTro, Integer> {

    Optional<VaiTro> findByTenVaiTro(String tenVaiTro);

}
