package com.example.sport_be.service.impl;

import com.example.sport_be.model.Category;
import com.example.sport_be.repository.CategoryRepository;
import com.example.sport_be.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Map<String, Object>> getAllCategories() {
        return categoryRepository.getAllCategoriesCustom();
    }

    @Override
    public Map<String, Object> getCategoryById(Integer id) {
        Map<String, Object> result = categoryRepository.getCategoryByIdCustom(id);
        if (result == null) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        return result;
    }

    @Override
    public Map<String, Object> createCategory(Category request) {
        validateCategory(request);
        request.setStatus(request.getStatus() != null ? request.getStatus() : true);
        Category savedCategory = categoryRepository.save(request);
        return categoryRepository.getCategoryByIdCustom(savedCategory.getId());
    }

    @Override
    public Map<String, Object> updateCategory(Integer id, Category request) {
        validateCategory(request);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        category.setName(request.getName());
        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }
        categoryRepository.save(category);
        return categoryRepository.getCategoryByIdCustom(id);
    }

    @Override
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private void validateCategory(Category request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }
        if (request.getName().length() > 150) {
            throw new IllegalArgumentException("Tên danh mục không vượt quá 150 ký tự");
        }
    }
}
