package com.example.sport_be.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HoanTienRequest {
    private String phuongThucHoan; // CHUYEN_KHOAN | TIEN_MAT | VNPAY
    private String soTkNhan;
    private String tenChuTk;
    private String nganHang;
    private String maGiaoDichHoan;
    private List<String> anhChungTu;
}
