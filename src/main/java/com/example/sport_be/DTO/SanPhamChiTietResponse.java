package com.example.sport_be.DTO;

import lombok.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamChiTietResponse {
    private Integer id;
    private Integer idSanPham;
    private Integer idKichThuoc;
    private Integer idMauSac;
    private Integer idChatLieu;
    private Integer soLuong;
    private BigDecimal giaBan;
    private Boolean trangThai;
}
