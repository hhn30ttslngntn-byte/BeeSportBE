package com.example.sport_be.repository;

import com.example.sport_be.entity.DoiTraChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DoiTraChiTietRepository extends JpaRepository<DoiTraChiTiet, Integer> {
    List<DoiTraChiTiet> findByDoiTraId(Integer doiTraId);

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO doi_tra_chi_tiet (id_doi_tra, id_hdct, so_luong_tra, gia_tri_hoan) " +
                    "VALUES (:doiTraId, :hoaDonChiTietId, :soLuongTra, :giaTriHoan)",
            nativeQuery = true
    )
    void insertDoiTraChiTiet(
            @Param("doiTraId") Integer doiTraId,
            @Param("hoaDonChiTietId") Integer hoaDonChiTietId,
            @Param("soLuongTra") Integer soLuongTra,
            @Param("giaTriHoan") BigDecimal giaTriHoan
    );

    @Query("SELECT COALESCE(SUM(dtct.soLuongTra), 0) FROM DoiTraChiTiet dtct WHERE dtct.hoaDonChiTiet.id = :hdctId")
    Integer sumSoLuongTraByHoaDonChiTietId(@Param("hdctId") Integer hdctId);
}
