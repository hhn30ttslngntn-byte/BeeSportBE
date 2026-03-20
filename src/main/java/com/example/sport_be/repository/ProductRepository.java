package com.example.sport_be.repository;

import com.example.sport_be.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    @Query("SELECT new map(p.id as id, c.name as categoryName, p.name as name, p.originalPrice as originalPrice, p.status as status) FROM Product p LEFT JOIN p.category c")
    List<Map<String, Object>> getAllProductsCustom();

    @Query("SELECT new map(p.id as id, c.name as categoryName, p.name as name, p.originalPrice as originalPrice, p.status as status) FROM Product p LEFT JOIN p.category c WHERE p.id = :id")
    Map<String, Object> getProductByIdCustom(@Param("id") Integer id);
}
