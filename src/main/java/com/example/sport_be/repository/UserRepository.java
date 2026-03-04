package com.example.sport_be.repository;

import com.example.sport_be.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<NguoiDung, Integer> {

    boolean existsByEmail(String email);

    Optional<NguoiDung> findByEmail(String email);

    List<NguoiDung> findByVaiTro_Id(Integer roleId);
}
