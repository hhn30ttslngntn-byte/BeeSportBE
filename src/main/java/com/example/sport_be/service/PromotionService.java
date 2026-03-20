package com.example.sport_be.service;

import com.example.sport_be.model.Promotion;
import java.util.List;
import java.util.Map;

public interface PromotionService {
    List<Map<String, Object>> getAllPromotions();
    Map<String, Object> getPromotionById(Integer id);
    Map<String, Object> createPromotion(Promotion request);
    Map<String, Object> updatePromotion(Integer id, Promotion request);
    void deletePromotion(Integer id);
}
