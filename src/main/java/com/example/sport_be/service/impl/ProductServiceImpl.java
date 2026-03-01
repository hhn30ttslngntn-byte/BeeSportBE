package com.example.sport_be.service.impl;

import com.example.sport_be.entity.Category;
import com.example.sport_be.entity.Product;
import com.example.sport_be.repository.CategoryRepository;
import com.example.sport_be.repository.ProductRepository;
import com.example.sport_be.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<Map<String, Object>> getAllProducts() {
        return productRepository.getAllProductsCustom();
    }

    @Override
    public Map<String, Object> getProductById(Integer id) {
        Map<String, Object> result = productRepository.getProductByIdCustom(id);
        if (result == null) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        return result;
    }

    @Override
    public Map<String, Object> createProduct(Product request) {
        validateProduct(request);
        
        Category category = categoryRepository.findById(request.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));

        request.setCategory(category);
        request.setStatus(request.getStatus() != null ? request.getStatus() : true);
        Product savedProduct = productRepository.save(request);
        return productRepository.getProductByIdCustom(savedProduct.getId());
    }

    @Override
    public Map<String, Object> updateProduct(Integer id, Product request) {
        validateProduct(request);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));

        product.setCategory(category);
        product.setName(request.getName());
        product.setOriginalPrice(request.getOriginalPrice());
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        productRepository.save(product);
        return productRepository.getProductByIdCustom(id);
    }

    @Override
    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private void validateProduct(Product request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (request.getName().length() > 200) {
            throw new IllegalArgumentException("Tên sản phẩm không vượt quá 200 ký tự");
        }
        if (request.getOriginalPrice() == null || request.getOriginalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá gốc không được nhỏ hơn 0");
        }
        if (request.getCategory() == null || request.getCategory().getId() == null) {
            throw new IllegalArgumentException("Vui lòng cung cấp ID danh mục (category.id)");
        }
    }
}
