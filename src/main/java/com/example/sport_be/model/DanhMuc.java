package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "danh_muc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_danh_muc")
    private Integer id;

    @Column(name = "ma_danh_muc", length = 50)
    private String maDanhMuc;

    @Column(name = "ten_danh_muc", length = 150)
    private String tenDanhMuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "danhMuc", fetch = FetchType.LAZY)
    private List<SanPham> sanPhamList;
}
