package com.example.sport_be.dto;

import com.example.sport_be.entity.SanPham;
import com.example.sport_be.entity.SanPhamChiTiet;
import lombok.Data;
import java.util.List;

@Data
public class ProductSaveRequest {
    private SanPham product;
    private List<SanPhamChiTiet> variants;
    private List<String> imageUrls;
}
