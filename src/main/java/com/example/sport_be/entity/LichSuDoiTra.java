package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_doi_tra")
@Getter
@Setter
public class LichSuDoiTra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lsdt")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_doi_tra")
    private DoiTra doiTra;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @Column(name = "hanhDong", length = 100)
    private String hanhDong;

    @Column(name = "chiTiet", columnDefinition = "NVARCHAR(MAX)")
    private String chiTiet;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }
}
