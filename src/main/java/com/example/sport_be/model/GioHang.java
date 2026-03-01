package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "gio_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gio_hang")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung")
    private NguoiDung nguoiDung;

    @Column(name = "trang_thai", length = 30)
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @OneToMany(mappedBy = "gioHang", fetch = FetchType.LAZY)
    private List<GioHangChiTiet> chiTietList;
}
