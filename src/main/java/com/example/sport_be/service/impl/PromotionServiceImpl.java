package com.example.sport_be.service.impl;

import com.example.sport_be.model.Promotion;
import com.example.sport_be.repository.PromotionRepository;
import com.example.sport_be.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    public List<Map<String, Object>> getAllPromotions() {
        return promotionRepository.getAllPromotionsCustom();
    }

    @Override
    public Map<String, Object> getPromotionById(Integer id) {
        Map<String, Object> result = promotionRepository.getPromotionByIdCustom(id);
        if (result == null) {
            throw new IllegalArgumentException("Promotion not found with id: " + id);
        }
        return result;
    }

    @Override
    public Map<String, Object> createPromotion(Promotion request) {
        validatePromotion(request);
        request.setUsedQuantity(0);
        request.setStatus(request.getStatus() != null ? request.getStatus() : true);
        Promotion savedPromotion = promotionRepository.save(request);
        return promotionRepository.getPromotionByIdCustom(savedPromotion.getId());
    }

    @Override
    public Map<String, Object> updatePromotion(Integer id, Promotion request) {
        validatePromotion(request);

        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found with id: " + id));

        promotion.setCode(request.getCode());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setMaxDiscountValue(request.getMaxDiscountValue());
        promotion.setMinOrderValue(request.getMinOrderValue());
        promotion.setQuantity(request.getQuantity());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        if (request.getStatus() != null) {
            promotion.setStatus(request.getStatus());
        }

        promotionRepository.save(promotion);
        return promotionRepository.getPromotionByIdCustom(id);
    }

    @Override
    public void deletePromotion(Integer id) {
        if (!promotionRepository.existsById(id)) {
            throw new IllegalArgumentException("Promotion not found with id: " + id);
        }
        promotionRepository.deleteById(id);
    }

    private void validatePromotion(Promotion request) {
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khuyến mãi không được trống");
        }
        if (request.getDiscountType() == null || 
            (!request.getDiscountType().equals("PERCENT") && !request.getDiscountType().equals("AMOUNT"))) {
            throw new IllegalArgumentException("Kiểu giảm giá phải là PERCENT hoặc AMOUNT");
        }
        if (request.getDiscountValue() == null || request.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá trị giảm không hợp lệ");
        }
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng không hợp lệ");
        }
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getEndDate().isBefore(request.getStartDate())) {
                throw new IllegalArgumentException("Ngày kết thúc khuyến mãi không được trước ngày bắt đầu");
            }
        }
    }
}
