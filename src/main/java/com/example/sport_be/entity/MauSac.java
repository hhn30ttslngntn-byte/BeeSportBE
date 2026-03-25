package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mau_sac")
@Getter
@Setter
public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mau_sac")
    private Integer id;

    @Column(name = "ma_mau_sac", length = 50)
    private String ma;

    @Column(name = "ten_mau")
    private String ten;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    public String getTen() {
        if (ten == null) {
            return null;
        }

        String normalizedBrokenText = switch (ten.trim()) {
            case "Đ?" -> "Đỏ";
            case "Tr?ng" -> "Trắng";
            case "Den" -> "Đen";
            default -> ten;
        };

        // Fallback for legacy rows that were saved with broken Vietnamese text.
        if (!normalizedBrokenText.contains("?")) {
            return normalizedBrokenText;
        }

        if (ma == null) {
            return normalizedBrokenText;
        }

        return switch (ma.trim().toUpperCase()) {
            case "DEN" -> "Đen";
            case "TRANG" -> "Trắng";
            case "DO" -> "Đỏ";
            case "XANH" -> "Xanh";
            case "XANH_DUONG" -> "Xanh dương";
            case "XANH_LA" -> "Xanh lá";
            case "VANG" -> "Vàng";
            case "HONG" -> "Hồng";
            case "TIM" -> "Tím";
            case "CAM" -> "Cam";
            case "NAU" -> "Nâu";
            case "XAM" -> "Xám";
            default -> normalizedBrokenText;
        };
    }
}
