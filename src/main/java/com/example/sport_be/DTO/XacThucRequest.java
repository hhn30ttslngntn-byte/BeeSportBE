package com.example.sport_be.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XacThucRequest {
    private Integer idNguoiDung;
    private String loaiXacThuc;
    private Boolean trangThai;
}
