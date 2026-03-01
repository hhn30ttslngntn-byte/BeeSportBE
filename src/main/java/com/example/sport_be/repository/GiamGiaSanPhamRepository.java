package com.example.sport_be.repository;

import com.example.sport_be.model.GiamGiaSanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiamGiaSanPhamRepository extends JpaRepository<GiamGiaSanPham, Integer> {

    List<GiamGiaSanPham> findBySanPham_Id(Integer idSanPham);

}
