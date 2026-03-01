package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "mau_sac")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mau_sac")
    private Integer id;

    @Column(name = "ten_mau", length = 50)
    private String tenMau;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
