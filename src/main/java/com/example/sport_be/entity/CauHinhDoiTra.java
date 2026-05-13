package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cau_hinh_doi_tra")
@Getter
@Setter
public class CauHinhDoiTra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phi_xu_ly_phan_tram")
    private BigDecimal phiXuLyPhanTram;

    @Column(name = "phi_ship_hoan")
    private BigDecimal phiShipHoan;

    @Column(name = "so_ngay_cho_phep")
    private Integer soNgayChoPhep;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() {
        ngayCapNhat = LocalDateTime.now();
        if (phiXuLyPhanTram == null) phiXuLyPhanTram = new BigDecimal("5.00");
        if (phiShipHoan == null) phiShipHoan = new BigDecimal("30000");
        if (soNgayChoPhep == null) soNgayChoPhep = 7;
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
