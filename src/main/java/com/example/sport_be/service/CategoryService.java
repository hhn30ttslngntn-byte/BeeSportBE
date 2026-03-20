package com.example.sport_be.service;

import com.example.sport_be.model.Category;
import java.util.List;
import java.util.Map;

public interface CategoryService {
    List<Map<String, Object>> getAllCategories();
    Map<String, Object> getCategoryById(Integer id);
    Map<String, Object> createCategory(Category request);
    Map<String, Object> updateCategory(Integer id, Category request);
    void deleteCategory(Integer id);
}
