package com.example.sport_be.DTO;

import lombok.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonChiTietDTO {
    private Integer id;
    private Integer idHoaDon;
    private Integer idSpct;
    private Integer soLuong;
    private BigDecimal gia;
}
