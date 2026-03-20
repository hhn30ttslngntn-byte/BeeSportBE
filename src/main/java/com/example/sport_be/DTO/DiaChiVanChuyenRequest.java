package com.example.sport_be.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaChiVanChuyenRequest {

    @NotNull(message = "Người dùng không được null")
    private Integer idNguoiDung;

    @NotNull(message = "Xã không được null")
    private Integer idXa;

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 150)
    private String tenNguoiNhan;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 20)
    private String soDienThoai;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Size(max = 255)
    private String diaChiChiTiet;

    private Boolean laMacDinh;
}
