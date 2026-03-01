package com.example.sport_be.DTO;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhMucRequest {
    @NotBlank
    @Size(max = 150)
    private String tenDanhMuc;
}
