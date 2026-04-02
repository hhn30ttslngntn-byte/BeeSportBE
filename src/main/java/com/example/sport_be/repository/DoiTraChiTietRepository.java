package com.example.sport_be.repository;

import com.example.sport_be.entity.DoiTraChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoiTraChiTietRepository extends JpaRepository<DoiTraChiTiet, Integer> {
    List<DoiTraChiTiet> findByDoiTraId(Integer doiTraId);

    // Tính tổng số lượng đã trả cho 1 dòng hóa đơn chi tiết (dùng để validate không vượt quá)
    @Query("SELECT COALESCE(SUM(dtct.soLuongTra), 0) FROM DoiTraChiTiet dtct WHERE dtct.hoaDonChiTiet.id = :hdctId")
    Integer sumSoLuongTraByHoaDonChiTietId(@Param("hdctId") Integer hdctId);
}
