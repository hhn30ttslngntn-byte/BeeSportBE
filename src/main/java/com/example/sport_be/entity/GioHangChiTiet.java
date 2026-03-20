package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gio_hang_chi_tiet")
@Getter
@Setter
public class GioHangChiTiet {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY) 
@Column(name = "id_ghct")
private Integer idGhct;

    @Column(name = "ma_gio_hang_chi_tiet", unique = true)
    private String ma;

    @ManyToOne
    @JoinColumn(name = "id_gio_hang")
    private GioHang gioHang;

    @ManyToOne
    @JoinColumn(name = "id_spct")
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "don_gia")
    private BigDecimal donGia;

    @Column(name = "chon")
    private Boolean chon;

    @Column(name = "ngay_them")
    private LocalDateTime ngayThem;

    @PrePersist
    protected void onCreate() {
        if (ma == null) ma = "GHCT" + System.currentTimeMillis();
        if (ngayThem == null) ngayThem = LocalDateTime.now();
        if (chon == null) chon = true;
    }
}
