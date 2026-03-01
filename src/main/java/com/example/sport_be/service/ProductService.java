package com.example.sport_be.service;

import com.example.sport_be.model.Product;
import java.util.List;
import java.util.Map;

public interface ProductService {
    List<Map<String, Object>> getAllProducts();
    Map<String, Object> getProductById(Integer id);
    Map<String, Object> createProduct(Product request);
    Map<String, Object> updateProduct(Integer id, Product request);
    void deleteProduct(Integer id);
}
