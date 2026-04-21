package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "doi_tra_chi_tiet")
@Getter
@Setter
public class DoiTraChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doi_tra_ct")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_doi_tra")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private DoiTra doiTra;

    @ManyToOne
    @JoinColumn(name = "id_hdct")
    private HoaDonChiTiet hoaDonChiTiet;

    @Column(name = "so_luong_tra")
    private Integer soLuongTra;

    @Column(name = "don_gia")
    private BigDecimal donGia;

    @Column(name = "gia_tri_hoan")
    private BigDecimal giaTriHoan;

    @Column(name = "id_spct_moi")
    private Integer idSpctMoi; // ID của sản phẩm chi tiết mới nếu là EXCHANGE
}
