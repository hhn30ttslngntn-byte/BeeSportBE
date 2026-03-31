package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pt_thanh_toan")
@Getter
@Setter
public class PtThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pttt")
    private Integer id;

    @Column(name = "ma_pt_thanh_toan", length = 50)
    private String maPtThanhToan;

    @Column(name = "ten_pttt", length = 100)
    private String tenPttt;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @PrePersist
    protected void onCreate() {
        if (trangThai == null) trangThai = true;
    }
}
