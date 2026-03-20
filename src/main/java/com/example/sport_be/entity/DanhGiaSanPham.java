package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "danh_gia_san_pham")
@Getter
@Setter
public class DanhGiaSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_danh_gia")
    private Integer id;

    @Column(name = "ma_danh_gia", length = 50, unique = true)
    private String maDanhGia;

    @ManyToOne
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "id_san_pham", nullable = false)
    private SanPham sanPham;

    @Column(name = "so_sao")
    private Integer soSao;

    @Column(name = "noi_dung", length = 500)
    private String noiDung;

    @Column(name = "ngay_danh_gia")
    private LocalDateTime ngayDanhGia;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @PrePersist
    protected void onCreate() {
        ngayDanhGia = LocalDateTime.now();
        if (trangThai == null) trangThai = true;
        if (maDanhGia == null) maDanhGia = "DG" + System.currentTimeMillis();
    }
}
