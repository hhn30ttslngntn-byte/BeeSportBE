package com.example.sport_be.DTO;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DiaChiVanChuyenDTO {
    private Integer id;

    @NotNull
    private Integer idNguoiDung;

    @NotNull
    private Integer idXa;

    @NotBlank
    @Size(max = 255)
    private String diaChiChiTiet;
    private String maDiaChiVanChuyen;

    private Boolean trangThai;
}
