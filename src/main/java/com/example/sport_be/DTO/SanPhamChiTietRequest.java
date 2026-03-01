package com.example.sport_be.DTO;

import lombok.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamChiTietRequest {
    @NotNull
    private Integer idSanPham;

    @NotNull
    private Integer idKichThuoc;

    @NotNull
    private Integer idMauSac;

    @NotNull
    private Integer idChatLieu;

    @NotNull
    @Min(value = 0, message = "Số lượng không được âm")
    private Integer soLuong;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Giá bán phải lớn hơn 0")
    private BigDecimal giaBan;
    private Boolean trangThai;
}
