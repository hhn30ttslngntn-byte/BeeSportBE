package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "thuong_hieu")
@Getter
@Setter
public class ThuongHieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thuong_hieu")
    private Integer id;

    @Column(name = "ma_thuong_hieu", length = 50, unique = true)
    private String maThuongHieu;

    @Column(name = "ten_thuong_hieu", length = 150, nullable = false)
    private String tenThuongHieu;

    @Column(name = "mo_ta", length = 255)
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @PrePersist
    protected void onCreate() {
        if (trangThai == null) trangThai = true;
    }
}
