package com.example.sport_be.DTO;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioHangResponse {
    private Integer id;
    private Integer idNguoiDung;
    private String trangThai;
    private LocalDateTime ngayTao;
}
