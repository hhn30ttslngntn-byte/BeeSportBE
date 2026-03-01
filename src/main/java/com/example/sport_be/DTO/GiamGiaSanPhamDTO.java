package com.example.sport_be.DTO;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiamGiaSanPhamDTO {
    private Integer id;

    @NotNull
    private Integer idDotGiamGia;

    @NotNull
    private Integer idSanPham;

    private Boolean trangThai;
}
