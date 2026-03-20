package com.example.sport_be.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CheckoutResponse {
    private BigDecimal tongTienHang;
    private BigDecimal tienGiam;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongThanhToan;
    private List<CartItemInfo> items;

    @Getter
    @Setter
    @Builder
    public static class CartItemInfo {
        private Integer spctId;
        private String tenSanPham;
        private String kichThuoc;
        private String mauSac;
        private String chatLieu;
        private BigDecimal donGia;
        private Integer soLuong;
        private BigDecimal thanhTien;
    }
}
