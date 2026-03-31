package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tinh")
@Getter
@Setter
public class Tinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tinh")
    private Integer id;

    @Column(name = "ma_tinh", length = 50)
    private String maTinh;

    @Column(name = "ten_tinh", length = 100)
    private String tenTinh;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @PrePersist
    protected void onCreate() {
        if (trangThai == null) trangThai = true;
    }
}
