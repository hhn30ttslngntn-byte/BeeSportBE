package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tinh")
    private Tinh tinh;

    @Column(name = "ma_huyen", length = 50)
    private String maHuyen;

    @Column(name = "ten_huyen", length = 100)
    private String tenHuyen;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
