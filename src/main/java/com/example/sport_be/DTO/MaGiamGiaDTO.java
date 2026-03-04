package com.example.sport_be.DTO;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaGiamGiaDTO {
    private Integer id;
    private String maCode;
    private String kieuGiamGia;
    private BigDecimal giaTriGiam;
    private BigDecimal giaTriGiamToiDa;
    private BigDecimal giaTriToiThieu;
    private Integer soLuong;
    private Integer soLuongDaDung;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Boolean trangThai;
}
