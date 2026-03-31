package com.example.sport_be.repository;

import com.example.sport_be.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {
    List<GioHangChiTiet> findByGioHangId(Integer gioHangId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO gio_hang_chi_tiet (id_gio_hang, id_spct, so_luong, don_gia, ma_gio_hang_chi_tiet, chon) " +
                   "VALUES (:idGioHang, :idSpct, :soLuong, :donGia, :ma, 1)", nativeQuery = true)
    void nativeAddToCart(@Param("idGioHang") Integer idGioHang, 
                         @Param("idSpct") Integer idSpct, 
                         @Param("soLuong") Integer soLuong, 
                         @Param("donGia") BigDecimal donGia,
                         @Param("ma") String ma);

    @Query(value = "SELECT SUM(ISNULL(ghct.don_gia, spct.gia_ban) * ghct.so_luong) " +
                   "FROM gio_hang_chi_tiet ghct " +
                   "JOIN san_pham_chi_tiet spct ON ghct.id_spct = spct.id_spct " +
                   "WHERE ghct.id_ghct IN :ids", nativeQuery = true)
    BigDecimal calculateTotal(@Param("ids") List<Integer> ids);
}
