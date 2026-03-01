package com.example.sport_be.DTO;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KichThuocDTO {
    private Integer id;

    @NotBlank
    @Size(max = 50)
    private String tenKichThuoc;

    private Boolean trangThai;
}
