package com.example.sport_be.DTO;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HuyenDTO {
    private Integer id;
    private Integer idTinh;
    private String tenHuyen;
    private Boolean trangThai;
}
