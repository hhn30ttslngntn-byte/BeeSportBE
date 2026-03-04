package com.example.sport_be.DTO;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamResponse {
    private Integer id;
    private Integer idDanhMuc;
    private String tenSanPham;
    private BigDecimal giaGoc;
    private Boolean trangThai;
}
