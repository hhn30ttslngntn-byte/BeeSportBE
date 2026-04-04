package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_thanh_toan")
@Getter
@Setter
public class LichSuThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lstt")
    private Integer id;

    @Column(name = "ma_lich_su_thanh_toan", length = 50)
    private String maLichSuThanhToan;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "id_pttt")
    private PtThanhToan ptThanhToan;

    @Column(name = "so_tien")
    private BigDecimal soTien;

    @Column(name = "trang_thai_thanh_toan", length = 30)
    private String trangThaiThanhToan;

    @Column(name = "vnp_TransactionNo", length = 50)
    private String vnpTransactionNo;

    @Column(name = "vnp_PayDate", length = 14)
    private String vnpPayDate;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @PrePersist
    protected void onCreate() {
        ngayThanhToan = LocalDateTime.now();
        if (maLichSuThanhToan == null) maLichSuThanhToan = "LSTT" + System.currentTimeMillis();
    }
}
