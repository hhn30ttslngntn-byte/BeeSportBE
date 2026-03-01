package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "san_pham")
@Getter
@Setter
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_san_pham")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_danh_muc")
    private DanhMuc danhMuc;

    @Column(name = "ten_san_pham", length = 200)
    private String ten;

    @Column(name = "gia_goc")
    private BigDecimal giaGoc;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "sanPham")
    private List<HinhAnhSanPham> hinhAnhs;
}
