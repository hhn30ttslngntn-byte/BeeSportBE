package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "huyen")
@Getter
@Setter
public class Huyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_huyen")
    private Integer id;

    @Column(name = "ma_huyen", length = 50)
    private String maHuyen;

    @ManyToOne
    @JoinColumn(name = "id_tinh")
    private Tinh tinh;

    @Column(name = "ten_huyen", length = 100)
    private String tenHuyen;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @PrePersist
    protected void onCreate() {
        if (trangThai == null) trangThai = true;
    }
}
