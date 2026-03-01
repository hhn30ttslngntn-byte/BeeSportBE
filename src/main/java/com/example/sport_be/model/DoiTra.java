package com.example.sport_be.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "doi_tra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoiTra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doi_tra")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @Column(name = "ly_do", length = 255)
    private String lyDo;

    @Column(name = "trang_thai", length = 30)
    private String trangThai;

    @Column(name = "ngay_yeu_cau")
    private LocalDateTime ngayYeuCau;
}
