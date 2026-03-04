package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lich_su_hoa_don")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LichSuHoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ls_hd")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @Column(name = "ma_lich_su_hoa_don", length = 50)
    private String maLichSuHoaDon;

    @Column(name = "trang_thai", length = 30)
    private String trangThai;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}
