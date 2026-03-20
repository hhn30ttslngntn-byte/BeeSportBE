package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ma_giam_gia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ma_giam_gia")
    private Integer id;

    @Column(name = "ma_code", length = 50, unique = true)
    private String code;

    // PERCENT | AMOUNT
    @Column(name = "kieu_giam_gia", length = 20)
    private String discountType;

    @Column(name = "gia_tri_giam", precision = 18, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "gia_tri_giam_toi_da", precision = 18, scale = 2)
    private BigDecimal maxDiscountValue;

    @Column(name = "gia_tri_toi_thieu", precision = 18, scale = 2)
    private BigDecimal minOrderValue;

    @Column(name = "so_luong")
    private Integer quantity;

    @Column(name = "so_luong_da_dung")
    @Builder.Default
    private Integer usedQuantity = 0;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime startDate;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime endDate;

    @Column(name = "trang_thai")
    @Builder.Default
    private Boolean status = true;
}
