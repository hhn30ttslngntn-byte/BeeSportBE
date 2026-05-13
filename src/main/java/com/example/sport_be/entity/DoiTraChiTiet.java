package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private BigDecimal giaTriHoan;

    @Column(name = "sku_doi_chieu", length = 100)
    private String skuDoiChieu;

    @Column(name = "ket_qua_kiem", length = 20)
    private String ketQuaKiem;

    @Column(name = "checklist_json", columnDefinition = "NVARCHAR(MAX)")
    private String checklistJson;

    @Column(name = "anh_kiem", columnDefinition = "NVARCHAR(MAX)")
    private String anhKiem;

    @Column(name = "nguoi_kiem", length = 100)
    private String nguoiKiem;

    @Column(name = "thoi_gian_kiem")
    private LocalDateTime thoiGianKiem;

    @Column(name = "nguoi_duyet", length = 100)
    private String nguoiDuyet;

    @Column(name = "thoi_gian_duyet")
    private LocalDateTime thoiGianDuyet;

    @Column(name = "ghi_chu_kiem", length = 500)
    private String ghiChuKiem;
}
