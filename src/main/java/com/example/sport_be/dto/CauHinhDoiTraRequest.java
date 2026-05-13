package com.example.sport_be.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CauHinhDoiTraRequest {
    private BigDecimal phiXuLyPhanTram;
    private BigDecimal phiShipHoan;
    private Integer soNgayChoPhep;
}
