package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "pt_thanh_toan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PhuongThucThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pttt")
    private Integer id;

    @Column(name = "ma_pt_thanh_toan", length = 50)
    private String maPTThanhToan;

    @Column(name = "ten_pttt", length = 100)
    private String tenPttt;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
