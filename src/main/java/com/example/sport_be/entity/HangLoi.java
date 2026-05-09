package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "hang_loi")
@Getter
@Setter
public class HangLoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hang_loi")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_spct")
    private SanPhamChiTiet sanPhamChiTiet;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "ly_do")
    private String lyDo;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();
}
