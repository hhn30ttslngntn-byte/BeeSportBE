package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "san_pham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_san_pham")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc")
    private DanhMuc danhMuc;

    @Column(name = "ten_san_pham", length = 200)
    private String tenSanPham;

    @Column(name = "gia_goc")
    private BigDecimal giaGoc;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "sanPham", fetch = FetchType.LAZY)
    private List<SanPhamChiTiet> chiTietList;

    @OneToMany(mappedBy = "sanPham", fetch = FetchType.LAZY)
    private List<HinhAnhSanPham> hinhAnhList;
}
