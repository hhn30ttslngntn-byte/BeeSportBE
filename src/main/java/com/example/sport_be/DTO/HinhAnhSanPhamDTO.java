package com.example.sport_be.DTO;
import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HinhAnhSanPhamDTO {
    private Integer id;

    @NotNull(message = "Sản phẩm không được null")
    private Integer idSanPham;

    @NotBlank(message = "URL không được để trống")
    @Size(max = 255, message = "URL tối đa 255 ký tự")
    private String url;

    private Boolean trangThai;
}
