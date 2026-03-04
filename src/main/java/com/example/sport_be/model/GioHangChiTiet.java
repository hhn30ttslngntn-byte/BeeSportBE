package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gio_hang_chi_tiet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class GioHangChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ghct")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gio_hang")
    private GioHang gioHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_spct")
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "so_luong")
    private Integer soLuong;
}
