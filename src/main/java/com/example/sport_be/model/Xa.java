package com.example.sport_be.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "xa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Xa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_xa")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_huyen")
    private Huyen huyen;

    @Column(name = "ten_xa", length = 100)
    private String tenXa;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
