package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "giam_gia_san_pham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class GiamGiaSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_giam_gia_san_pham")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dot_giam_gia")
    private DotGiamGia dotGiamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham")
    private SanPham sanPham;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
