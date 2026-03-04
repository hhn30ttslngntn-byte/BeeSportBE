package com.example.sport_be.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioHangChiTietDTO {
    private Integer id;
    private Integer idGioHang;
    private Integer idSpct;
    private Integer soLuong;
}
