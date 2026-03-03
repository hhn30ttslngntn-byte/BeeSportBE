package com.example.sport_be.repository;

import com.example.sport_be.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    Optional<NguoiDung> findByEmail(String email);
    Optional<NguoiDung> findBySoDienThoai(String soDienThoai);
    Boolean existsByEmail(String email);
    Boolean existsBySoDienThoai(String soDienThoai);
    List<NguoiDung> findByVaiTroId(Integer roleId);
}
