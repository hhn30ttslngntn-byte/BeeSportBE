package com.example.sport_be.repository;

import com.example.sport_be.entity.Xa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XaRepository extends JpaRepository<Xa, Integer> {
    List<Xa> findByHuyenIdAndTrangThaiTrueOrderByTenXaAsc(Integer huyenId);
}
