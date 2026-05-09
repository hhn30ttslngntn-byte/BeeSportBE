package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_lieu")
@Getter
@Setter
public class ChatLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chat_lieu")
    private Integer id;

    @Column(name = "ma_chat_lieu", length = 50)
    private String ma;

    @Column(name = "ten_chat_lieu")
    private String ten;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    public String getTen() {
        if (ten == null) return null;
        if (!ten.contains("?")) return ten;
        
        if (ma == null) return ten;
        return switch (ma.trim().toUpperCase()) {
            case "COTTON" -> "Cotton";
            case "POLY" -> "Poly";
            case "DUI" -> "Đũi";
            case "KATE" -> "Kate";
            case "NILON" -> "Nilon";
            case "THUN" -> "Thun";
            default -> ma.trim().toUpperCase();
        };
    }
}
