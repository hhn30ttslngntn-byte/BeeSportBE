package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "san_pham")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_san_pham")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc")
    private Category category;

    @Column(name = "ten_san_pham", length = 200)
    private String name;

    @Column(name = "gia_goc", precision = 18, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "trang_thai")
    private Boolean status;
}
