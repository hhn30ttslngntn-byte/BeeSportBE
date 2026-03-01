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
public class DotGiamGiaDTO {
    private Integer id;

    @NotBlank
    private String tenDot;

    @NotBlank
    private String kieuGiamGia;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal giaTriGiam;

    @NotNull
    private LocalDateTime ngayBatDau;

    @NotNull
    private LocalDateTime ngayKetThuc;

    private Boolean trangThai;
}
