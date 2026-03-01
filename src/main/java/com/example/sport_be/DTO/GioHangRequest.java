package com.example.sport_be.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioHangRequest {
    @NotNull
    private Integer idNguoiDung;
}
