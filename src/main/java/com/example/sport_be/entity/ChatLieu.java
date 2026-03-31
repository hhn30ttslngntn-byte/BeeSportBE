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

    @Column(name = "ten_chat_lieu")
    private String ten;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
