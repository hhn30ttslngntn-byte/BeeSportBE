package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_hoa_don")
@Getter
@Setter
public class LichSuHoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ls_hd")
    private Integer id;

    @Column(name = "ma_lich_su", length = 50, unique = true)
    private String maLichSu;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private HoaDon hoaDon;

    @Column(name = "trang_thai_cu", length = 30)
    private String trangThaiCu;

    @Column(name = "trang_thai_moi", length = 30)
    private String trangThaiMoi;

    @Column(name = "loai_hanh_dong", length = 50)
    private String loaiHanhDong;

    @Column(name = "hanh_dong", length = 255)
    private String hanhDong;

    @ManyToOne
    @JoinColumn(name = "id_nguoi_thuc_hien")
    private NguoiDung nguoiThucHien;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @PrePersist
    protected void onCreate() {
        thoiGian = LocalDateTime.now();
        if (maLichSu == null) maLichSu = "LSHD_" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
