package com.example.sport_be.DTO;

import lombok.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuSuDungMaGiamGiaDTO {
    private Integer id;
    private Integer idMaGiamGia;
    private Integer idHoaDon;
    private LocalDateTime ngaySuDung;
}
