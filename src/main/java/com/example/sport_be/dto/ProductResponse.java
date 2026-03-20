package com.example.sport_be.dto;

import com.example.sport_be.entity.DanhMuc;
import com.example.sport_be.entity.ThuongHieu;
import com.example.sport_be.entity.HinhAnhSanPham;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Integer id;
    private String ma;
    private String tenSanPham;
    private DanhMuc danhMuc;
    private ThuongHieu thuongHieu;
    private BigDecimal giaGoc;
    private BigDecimal giaBanMin; // New field for variants' selling price
    private Boolean trangThai;
    private List<HinhAnhSanPham> hinhAnhs;
}
