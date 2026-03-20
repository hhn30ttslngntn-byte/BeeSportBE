package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "san_pham_yeu_thich")
@Getter
@Setter
public class SanPhamYeuThich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_yeu_thich")
    private Integer id;

    @Column(name = "ma_san_pham_yeu_thich", length = 50, unique = true)
    private String maSanPhamYeuThich;

    @ManyToOne
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "id_san_pham", nullable = false)
    private SanPham sanPham;

    @Column(name = "ngay_them")
    private LocalDateTime ngayThem;

    @PrePersist
    protected void onCreate() {
        ngayThem = LocalDateTime.now();
        if (maSanPhamYeuThich == null) maSanPhamYeuThich = "YT" + System.currentTimeMillis();
    }
}
