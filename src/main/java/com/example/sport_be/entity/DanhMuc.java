package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "danh_muc")
@Getter
@Setter
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_danh_muc")
    private Integer id;

    @Column(name = "ten_danh_muc", length = 150)
    private String ten;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
