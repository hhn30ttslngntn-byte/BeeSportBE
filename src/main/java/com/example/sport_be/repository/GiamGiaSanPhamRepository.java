package com.example.sport_be.repository;

import com.example.sport_be.entity.GiamGiaSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface GiamGiaSanPhamRepository extends JpaRepository<GiamGiaSanPham, Integer> {
    List<GiamGiaSanPham> findByDotGiamGiaId(Integer dotGiamGiaId);
    List<GiamGiaSanPham> findBySanPhamChiTietId(Integer spctId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM GiamGiaSanPham ggsp WHERE ggsp.dotGiamGia.id = ?1")
    void deleteByDotGiamGiaId(Integer dotGiamGiaId);
}
