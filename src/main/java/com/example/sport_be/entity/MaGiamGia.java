package com.example.sport_be.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ma_giam_gia")
@Getter
@Setter
public class MaGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ma_giam_gia")
    private Integer id;

    @Nationalized
    @Column(name = "ma_code", length = 50, unique = true)
    private String maCode;

    @Column(name = "kieu_giam_gia")
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
    private LocalDateTime ngayKetHuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
