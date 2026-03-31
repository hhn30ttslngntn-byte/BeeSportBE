package com.example.sport_be.repository;

import com.example.sport_be.entity.DiaChiVanChuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaChiVanChuyenRepository extends JpaRepository<DiaChiVanChuyen, Integer> {
    List<DiaChiVanChuyen> findByNguoiDungIdAndTrangThaiTrueOrderByLaMacDinhDescIdDesc(Integer userId);
    Optional<DiaChiVanChuyen> findByIdAndNguoiDungId(Integer id, Integer userId);
    List<DiaChiVanChuyen> findByNguoiDungId(Integer userId);
}
