package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "hinh_anh_san_pham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class HinhAnhSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hinh_anh")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham")
    private SanPham sanPham;

    @Column(name = "url", length = 255)
    private String url;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
