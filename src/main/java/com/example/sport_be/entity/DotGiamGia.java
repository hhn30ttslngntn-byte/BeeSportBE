package com.example.sport_be.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dot_giam_gia")
@Getter
@Setter
public class DotGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dot_giam_gia")
    private Integer id;

    @Column(name = "ma_dot_giam_gia", length = 50, unique = true)
    private String maDotGiamGia;

    @Column(name = "ten_dot", length = 150)
    private String tenDot;

    @Column(name = "kieu_giam_gia", length = 20)
    private String kieuGiamGia;

    @Column(name = "gia_tri_giam")
    private BigDecimal giaTriGiam;

    @Column(name = "ngay_bat_dau")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime ngayKetHuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @PrePersist
    protected void onCreate() {
        if (trangThai == null) trangThai = true;
        if (maDotGiamGia == null) maDotGiamGia = "DGG" + System.currentTimeMillis();
    }
}
