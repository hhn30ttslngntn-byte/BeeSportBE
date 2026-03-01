package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hoa_don")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hoa_don")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung")
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ma_giam_gia")
    private MaGiamGia maGiamGia;

    @Column(name = "tong_tien")
    private BigDecimal tongTien;

    @Column(name = "trang_thai_don", length = 30)
    private String trangThaiDon;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY)
    private List<HoaDonChiTiet> chiTietList;

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY)
    private List<LichSuHoaDon> lichSuList;

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY)
    private List<DoiTra> doiTraList;
}
