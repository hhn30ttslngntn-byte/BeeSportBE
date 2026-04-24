package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "giam_gia_san_pham")
@Getter
@Setter
public class GiamGiaSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_giam_gia_san_pham")
    private Integer id;

    @Column(name = "ma_giam_gia_san_pham", length = 50)
    private String maGiamGiaSanPham;

    @ManyToOne
    @JoinColumn(name = "id_dot_giam_gia")
    private DotGiamGia dotGiamGia;

    @ManyToOne
    @JoinColumn(name = "id_spct")
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @PrePersist
    protected void onCreate() {
        if (trangThai == null) trangThai = true;
        if (maGiamGiaSanPham == null) maGiamGiaSanPham = "GGSP" + System.currentTimeMillis();
    }
}
