package com.example.sport_be.dto;

import com.example.sport_be.entity.DotGiamGia;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PromotionSaveRequest {
    private DotGiamGia promotion;
    private List<Integer> productIds;
}
