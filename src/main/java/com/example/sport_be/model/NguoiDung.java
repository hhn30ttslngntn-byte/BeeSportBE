package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "nguoi_dung")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nguoi_dung")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vai_tro")
    private VaiTro vaiTro;

    @Column(name = "ma_nguoi_dung", length = 50)
    private String maNguoiDung;

    @Column(name = "ho_ten", length = 150)
    private String hoTen;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "mat_khau", length = 255)
    private String matKhau;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @OneToMany(mappedBy = "nguoiDung", fetch = FetchType.LAZY)
    private List<HoaDon> hoaDonList;

    @OneToMany(mappedBy = "nguoiDung", fetch = FetchType.LAZY)
    private List<GioHang> gioHangList;

    @OneToMany(mappedBy = "nguoiDung", fetch = FetchType.LAZY)
    private List<DiaChiVanChuyen> diaChiList;

    @OneToMany(mappedBy = "nguoiDung", fetch = FetchType.LAZY)
    private List<XacThuc> xacThucList;
}
