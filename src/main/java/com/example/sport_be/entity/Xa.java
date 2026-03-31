package com.example.sport_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "xa")
@Getter
@Setter
public class Xa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_xa")
    private Integer id;

    @Column(name = "ma_xa", length = 50)
    private String maXa;

    @ManyToOne
    @JoinColumn(name = "id_huyen")
    private Huyen huyen;

    @Column(name = "ten_xa", length = 100)
    private String tenXa;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @PrePersist
    protected void onCreate() {
        if (trangThai == null) trangThai = true;
    }
}
