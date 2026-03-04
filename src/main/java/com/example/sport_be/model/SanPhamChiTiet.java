package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "san_pham_chi_tiet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SanPhamChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_spct")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham")
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kich_thuoc")
    private KichThuoc kichThuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mau_sac")
    private MauSac mauSac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat_lieu")
    private ChatLieu chatLieu;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "gia_ban")
    private BigDecimal giaBan;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
