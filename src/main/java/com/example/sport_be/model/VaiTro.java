package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "vai_tro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaiTro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vai_tro")
    private Integer id;

    @Column(name = "ma_vai_tro", length = 50)
    private String maVaiTro;

    @Column(name = "ten_vai_tro", length = 50)
    private String tenVaiTro;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
