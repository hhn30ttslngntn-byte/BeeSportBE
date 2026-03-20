package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "kich_thuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class KichThuoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kich_thuoc")
    private Integer id;

    @Column(name = "ma_kich_thuoc", length = 50)
    private String maKichThuoc;

    @Column(name = "ten_kich_thuoc", length = 50)
    private String tenKichThuoc;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
