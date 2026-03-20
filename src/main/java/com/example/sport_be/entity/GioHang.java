package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "gio_hang")
@Getter
@Setter
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gio_hang")
    private Integer id;

    @Column(name = "ma_gio_hang", unique = true)
    private String ma;

    @Column(name = "loai_gio_hang")
    private String loaiGioHang;

    @ManyToOne
    @JoinColumn(name = "id_nguoi_dung")
    private NguoiDung nguoiDung;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        if (trangThai == null) trangThai = "DANG_SU_DUNG";
        if (ma == null) ma = "GH" + System.currentTimeMillis();
    }
}
