package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dot_giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DotGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dot_giam_gia")
    private Integer id;

    @Column(name = "ten_dot", length = 150)
    private String tenDot;

    @Column(name = "kieu_giam_gia", length = 20)
    private String kieuGiamGia;

    @Column(name = "gia_tri_giam")
    private BigDecimal giaTriGiam;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
