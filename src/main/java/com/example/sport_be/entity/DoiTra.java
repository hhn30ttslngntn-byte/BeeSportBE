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

    @Column(name = "tien_chenh_lech")
    private BigDecimal tienChenhLech;

    @Column(name = "phi_ship_hoan")
    private BigDecimal phiShipHoan;

    @Column(name = "ghi_chu_admin", length = 500)
    private String ghiChuAdmin;

    @Column(name = "trang_thai_thanh_toan", length = 30)
    private String trangThaiThanhToan; // CHUA_THANH_TOAN, DA_THANH_TOAN, KHONG_CAN_THANH_TOAN

    @Column(name = "ngay_yeu_cau")
    private LocalDateTime ngayYeuCau;

    @OneToMany(mappedBy = "doiTra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoiTraChiTiet> chiTiets;

    @PrePersist
    protected void onCreate() {
        ngayYeuCau = LocalDateTime.now();
        if (maDoiTra == null) maDoiTra = "DT" + System.currentTimeMillis();
    }
}
