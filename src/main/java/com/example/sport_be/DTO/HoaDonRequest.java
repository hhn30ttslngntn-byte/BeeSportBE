package com.example.sport_be.DTO;

import lombok.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonRequest {
    @NotNull(message = "Người dùng không được null")
    private Integer idNguoiDung;

    private Integer idMaGiamGia;
}
