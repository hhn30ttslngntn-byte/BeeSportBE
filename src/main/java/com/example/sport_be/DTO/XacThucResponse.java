package com.example.sport_be.DTO;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XacThucResponse {
    private Integer id;
    private Integer idNguoiDung;
    private String loaiXacThuc;
    private Boolean trangThai;
}
