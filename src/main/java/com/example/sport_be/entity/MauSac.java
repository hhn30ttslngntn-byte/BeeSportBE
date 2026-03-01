package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mau_sac")
@Getter
@Setter
public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mau_sac")
    private Integer id;

    @Column(name = "ten_mau")
    private String ten;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
