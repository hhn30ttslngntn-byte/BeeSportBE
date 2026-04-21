package com.example.sport_be.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoiTraChiTietRequest {
    private Integer hoaDonChiTietId; // id_hdct
    private Integer soLuongTra;
    private Integer idSpctMoi; // id_spct mới khách muốn đổi (nếu có)
}
