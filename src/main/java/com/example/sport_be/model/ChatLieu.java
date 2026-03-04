package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "chat_lieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chat_lieu")
    private Integer id;

    @Column(name = "ma_chat_lieu", length = 50)
    private String maChatLieu;

    @Column(name = "ten_chat_lieu", length = 50)
    private String tenChatLieu;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
