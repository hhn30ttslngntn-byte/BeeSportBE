package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "kich_thuoc")
@Getter
@Setter
public class KichThuoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kich_thuoc")
    private Integer id;

    @Column(name = "ma_kich_thuoc", length = 50)
    private String ma;

    @Column(name = "ten_kich_thuoc")
    private String ten;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    public String getTen() {
        if (ten == null) return null;
        
        // Return original if no broken characters
        if (!ten.contains("?")) return ten;
        
        // Fallback to code if name is broken
        if (ma == null) return ten;
        return ma.trim().toUpperCase();
    }
}
