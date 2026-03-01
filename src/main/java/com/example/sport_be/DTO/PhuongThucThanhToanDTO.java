package com.example.sport_be.DTO;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhuongThucThanhToanDTO {
    private Integer id;
    private String tenPttt;
    private Boolean trangThai;
}
