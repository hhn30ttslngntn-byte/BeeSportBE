package com.example.sport_be.DTO;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MauSacDTO {
    private Integer id;

    @NotBlank
    @Size(max = 50)
    private String tenMau;

    private Boolean trangThai;
}
