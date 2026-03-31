package com.example.sport_be.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Integer userId;
    private List<Integer> cartItemIds;
    private Integer voucherId;
    private String tenNguoiNhan;
    private String soDienThoai;
    private String tinh;
    private String huyen;
    private String xa;
    private String diaChiChiTiet;
    private String ghiChu;
}
