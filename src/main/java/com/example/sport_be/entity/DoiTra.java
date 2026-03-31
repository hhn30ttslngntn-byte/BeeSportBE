package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "doi_tra")
@Getter
@Setter
public class DoiTra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doi_tra")
    private Integer id;

    @Column(name = "ma_doi_tra", length = 50)
    private String maDoiTra;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @Column(name = "ly_do", length = 255)
    private String lyDo;

    @Column(name = "trang_thai", length = 30)
    private String trangThai;

    @Column(name = "ngay_yeu_cau")
    private LocalDateTime ngayYeuCau;

    @PrePersist
    protected void onCreate() {
        ngayYeuCau = LocalDateTime.now();
        if (maDoiTra == null) maDoiTra = "DT" + System.currentTimeMillis();
    }
}
