package com.example.sport_be.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaChiVanChuyenResponse {

    private Integer id;

    private String tenNguoiNhan;

    private String soDienThoai;

    private String diaChiChiTiet;

    private String xa;

    private String huyen;

    private String tinh;

    private Boolean laMacDinh;

    private Boolean trangThai;
}
