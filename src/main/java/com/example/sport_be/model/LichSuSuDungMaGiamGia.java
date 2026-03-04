package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_su_dung_ma_giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LichSuSuDungMaGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ls")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ma_giam_gia")
    private MaGiamGia maGiamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @Column(name = "ngay_su_dung")
    private LocalDateTime ngaySuDung;
}
