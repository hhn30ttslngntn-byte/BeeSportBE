package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "huyen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Huyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_huyen")
    private Integer id;
    @Column(name = "ma_huyen")
    private String maHuyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tinh")
    private Tinh tinh;

    @Column(name = "ten_huyen", length = 100)
    private String tenHuyen;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
