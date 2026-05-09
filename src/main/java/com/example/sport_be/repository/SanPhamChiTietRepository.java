package com.example.sport_be.repository;

import com.example.sport_be.entity.SanPhamChiTiet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {
    List<SanPhamChiTiet> findBySanPhamId(Integer sanPhamId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.id IN :ids")
    List<SanPhamChiTiet> findAllByIdInForUpdate(@Param("ids") List<Integer> ids);
}
