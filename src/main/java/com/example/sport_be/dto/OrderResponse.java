package com.example.sport_be.dto;

import com.example.sport_be.entity.HoaDon;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderResponse {
    private HoaDon hoaDon;
    private String paymentUrl;
}
