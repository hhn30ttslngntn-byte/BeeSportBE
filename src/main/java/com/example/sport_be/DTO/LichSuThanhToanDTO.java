package com.example.sport_be.DTO;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuThanhToanDTO {
    private Integer id;
    private Integer idHoaDon;
    private Integer idPttt;
    private BigDecimal soTien;
    private String trangThaiThanhToan;
    private LocalDateTime ngayThanhToan;
}
