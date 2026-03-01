package com.example.sport_be.DTO;

import lombok.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamRequest {
    @NotNull(message = "Danh mục không được null")
    private Integer idDanhMuc;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 200)
    private String tenSanPham;

    @NotNull(message = "Giá gốc không được null")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Giá phải lớn hơn 0")
    private BigDecimal giaGoc;
}
