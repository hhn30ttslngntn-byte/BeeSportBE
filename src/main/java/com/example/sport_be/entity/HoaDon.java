package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
@Getter
@Setter
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hoa_don")
    private Integer id;

    @Column(name = "ma_hoa_don")
    private String maHoaDon;

    @ManyToOne
    @JoinColumn(name = "id_nguoi_dung")
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "id_ma_giam_gia")
    private MaGiamGia maGiamGia;

    @ManyToOne
    @JoinColumn(name = "id_pttt")
    private PtThanhToan ptThanhToan;

    @Column(name = "ten_nguoi_nhan")
    private String tenNguoiNhan;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "tinh")
    private String tinh;

    @Column(name = "huyen")
    private String huyen;

    @Column(name = "xa")
    private String xa;

    @Column(name = "dia_chi_chi_tiet")
    private String diaChiChiTiet;

    @Column(name = "tong_tien_hang")
    private BigDecimal tongTienHang = BigDecimal.ZERO;

    @Column(name = "tien_giam")
    private BigDecimal tienGiam = BigDecimal.ZERO;

    @Column(name = "phi_van_chuyen")
    private BigDecimal phiVanChuyen = BigDecimal.ZERO;

    @Column(name = "tong_thanh_toan")
    private BigDecimal tongThanhToan = BigDecimal.ZERO;

    @Column(name = "trang_thai_don")
    private String trangThaiDon;

    @Column(name = "hoan_tra")
    private LocalDateTime hoanTra;

    @Column(name = "ngay_nhan_hang")
    private LocalDateTime ngayNhanHang;

    @Column(name = "loai_don_hang")
    private String loaiDonHang;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
        if (trangThaiDon == null) trangThaiDon = "CHO_XAC_NHAN";
        if (maHoaDon == null) maHoaDon = "HD" + System.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
