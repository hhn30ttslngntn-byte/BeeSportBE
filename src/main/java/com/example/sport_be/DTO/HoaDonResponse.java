package com.example.sport_be.DTO;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonResponse {
    private Integer id;
    private Integer idNguoiDung;
    private Integer idMaGiamGia;
    private BigDecimal tongTien;
    private String trangThaiDon;
    private LocalDateTime ngayTao;
}
