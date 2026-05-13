package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "doi_tra")
@Getter
@Setter
public class DoiTra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doi_tra")
    private Integer id;

    @Column(name = "ma_doi_tra", length = 50)
    private String maDoiTra;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @Column(name = "ly_do", length = 255)
    private String lyDo;

    @Column(name = "ly_do_tu_choi", length = 255)
    private String lyDoTuChoi;

    @Column(name = "tinh_trang_hang", length = 20)
    private String tinhTrangHang;

    @Column(name = "danh_sach_anh", columnDefinition = "NVARCHAR(MAX)")
    private String danhSachAnh;

    @Column(name = "tong_tien_hoan")
    private BigDecimal tongTienHoan;

    @Column(name = "trang_thai", length = 30)
    private String trangThai;

    @Column(name = "loai_doi_tra", length = 20)
    private String loaiDoiTra;

    @Column(name = "id_spct_moi")
    private Integer idSpctMoi;

    @Column(name = "ngay_xu_ly")
    private LocalDateTime ngayXuLy;

    @Column(name = "ben_chiu_loi", length = 10)
    private String benChiuLoi;

    @Column(name = "tien_hang_hoan")
    private BigDecimal tienHangHoan;

    @Column(name = "phi_xu_ly")
    private BigDecimal phiXuLy;

    @Column(name = "phi_ship_hoan_tru")
    private BigDecimal phiShipHoanTru;

    @Column(name = "phuong_thuc_hoan", length = 20)
    private String phuongThucHoan;

    @Column(name = "so_tk_nhan", length = 50)
    private String soTkNhan;

    @Column(name = "ten_chu_tk", length = 100)
    private String tenChuTk;

    @Column(name = "ngan_hang", length = 100)
    private String nganHang;

    @Column(name = "ma_giao_dich_hoan", length = 100)
    private String maGiaoDichHoan;

    @Column(name = "anh_chung_tu", columnDefinition = "NVARCHAR(MAX)")
    private String anhChungTu;

    @Column(name = "khach_xac_nhan_nhan_tien")
    private Boolean khachXacNhanNhanTien;

    @Column(name = "ngay_khach_xac_nhan")
    private LocalDateTime ngayKhachXacNhan;

    @Column(name = "token_xac_nhan", length = 100)
    private String tokenXacNhan;

    @Column(name = "ghi_chu_admin", length = 500)
    private String ghiChuAdmin;

    @Column(name = "ngay_yeu_cau")
    private LocalDateTime ngayYeuCau;

    @OneToMany(mappedBy = "doiTra")
    private List<DoiTraChiTiet> chiTiets;

    @PrePersist
    protected void onCreate() {
        ngayYeuCau = LocalDateTime.now();
        if (maDoiTra == null) maDoiTra = "DT" + System.currentTimeMillis();
        if (benChiuLoi == null || benChiuLoi.isBlank()) benChiuLoi = "KHACH";
        if (khachXacNhanNhanTien == null) khachXacNhanNhanTien = false;
        if (tienHangHoan == null) tienHangHoan = BigDecimal.ZERO;
        if (phiXuLy == null) phiXuLy = BigDecimal.ZERO;
        if (phiShipHoanTru == null) phiShipHoanTru = BigDecimal.ZERO;
        if (tongTienHoan == null) tongTienHoan = BigDecimal.ZERO;
    }
}
