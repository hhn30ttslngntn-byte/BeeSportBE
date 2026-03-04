package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tinh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Tinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tinh")
    private Integer id;

    @Column(name = "ten_tinh", length = 100)
    private String tenTinh;

    @Column(name = "trang_thai")
    private Boolean trangThai;
    @Column(name = "ma_tinh")
    private String maTinh;
}
