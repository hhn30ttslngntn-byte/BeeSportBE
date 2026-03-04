package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "xac_thuc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class XacThuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_xac_thuc")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung")
    private NguoiDung nguoiDung;

    @Column(name = "loai_xac_thuc", length = 50)
    private String loaiXacThuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
