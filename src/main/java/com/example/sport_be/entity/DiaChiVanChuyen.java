package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dia_chi_van_chuyen")
@Getter
@Setter
public class DiaChiVanChuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dia_chi")
    private Integer id;

    @Column(name = "ma_dia_chi_van_chuyen", length = 50)
    private String maDiaChiVanChuyen;

    @ManyToOne
    @JoinColumn(name = "id_nguoi_dung")
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "id_xa")
    private Xa xa;

    @Column(name = "ten_nguoi_nhan", length = 150)
    private String tenNguoiNhan;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "dia_chi_chi_tiet", length = 255)
    private String diaChiChiTiet;

    @Column(name = "loai_dia_chi", length = 30)
    private String loaiDiaChi;

    @Column(name = "la_mac_dinh")
    private Boolean laMacDinh;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @PrePersist
    protected void onCreate() {
        if (trangThai == null) trangThai = true;
        if (laMacDinh == null) laMacDinh = false;
        if (maDiaChiVanChuyen == null) maDiaChiVanChuyen = "DC" + System.currentTimeMillis();
    }
}
