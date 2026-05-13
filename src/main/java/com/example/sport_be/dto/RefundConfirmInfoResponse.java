package com.example.sport_be.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RefundConfirmInfoResponse {
    private String maDoiTra;
    private BigDecimal tongTienHoan;
    private BigDecimal tienHangHoan;
    private BigDecimal phiXuLy;
    private BigDecimal phiShipHoanTru;
    private String phuongThucHoan;
    private String maGiaoDichHoan;
    private String anhChungTu;
    private String trangThai;
}
