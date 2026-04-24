package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "gia_tri_hoan")
    private java.math.BigDecimal giaTriHoan;
}
