package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ma_giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MaGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ma_giam_gia")
    private Integer id;

    @Column(name = "ma_code", length = 50)
    private String maCode;

    @Column(name = "kieu_giam_gia", length = 20)
    private String kieuGiamGia;

    @Column(name = "gia_tri_giam")
    private BigDecimal giaTriGiam;

    @Column(name = "gia_tri_giam_toi_da")
    private BigDecimal giaTriGiamToiDa;

    @Column(name = "gia_tri_toi_thieu")
    private BigDecimal giaTriToiThieu;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "so_luong_da_dung")
    private Integer soLuongDaDung;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
