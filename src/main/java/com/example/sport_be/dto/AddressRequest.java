package com.example.sport_be.dto;

import lombok.Data;

@Data
public class AddressRequest {
    private Integer id;
    private String tenNguoiNhan;
    private String soDienThoai;
    private String diaChiChiTiet;
    private String loaiDiaChi;
    private Boolean laMacDinh;
    private Integer xaId;
}
