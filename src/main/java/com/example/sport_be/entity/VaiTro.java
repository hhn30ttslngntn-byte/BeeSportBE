package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vai_tro")
@Getter
@Setter
public class VaiTro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vai_tro")
    private Integer id;

    @Column(name = "ten_vai_tro", length = 50)
    private String ten;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
