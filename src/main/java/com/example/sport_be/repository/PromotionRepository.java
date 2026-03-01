package com.example.sport_be.repository;

import com.example.sport_be.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    
    @Query("SELECT new map(p.id as id, p.code as code, p.discountType as discountType, p.discountValue as discountValue, p.maxDiscountValue as maxDiscountValue, p.minOrderValue as minOrderValue, p.quantity as quantity, p.usedQuantity as usedQuantity, p.startDate as startDate, p.endDate as endDate, p.status as status) FROM Promotion p")
    List<Map<String, Object>> getAllPromotionsCustom();

    @Query("SELECT new map(p.id as id, p.code as code, p.discountType as discountType, p.discountValue as discountValue, p.maxDiscountValue as maxDiscountValue, p.minOrderValue as minOrderValue, p.quantity as quantity, p.usedQuantity as usedQuantity, p.startDate as startDate, p.endDate as endDate, p.status as status) FROM Promotion p WHERE p.id = :id")
    Map<String, Object> getPromotionByIdCustom(@Param("id") Integer id);
}
