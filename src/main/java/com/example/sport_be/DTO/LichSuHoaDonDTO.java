package com.example.sport_be.DTO;

import lombok.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuHoaDonDTO {
    private Integer id;
    private Integer idHoaDon;
    private String trangThai;
    private LocalDateTime ngayCapNhat;
}
