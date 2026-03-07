package com.example.sport_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hinh_anh_san_pham")
@Getter
@Setter
public class HinhAnhSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hinh_anh")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_san_pham")
    @JsonIgnore
    private SanPham sanPham;

    @Column(name = "url")
    private String url;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
