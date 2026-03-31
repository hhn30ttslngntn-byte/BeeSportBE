package com.example.sport_be.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private Integer id;
    private String tenNguoiNhan;
    private String soDienThoai;
    private String diaChiChiTiet;
    private String loaiDiaChi;
    private Boolean laMacDinh;
    private Boolean trangThai;
    private Integer xaId;
    private Integer huyenId;
    private Integer tinhId;
    private String xa;
    private String huyen;
    private String tinh;
}
