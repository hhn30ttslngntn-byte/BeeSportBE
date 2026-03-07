package com.example.sport_be.controller;

import com.example.sport_be.dto.OrderRequest;
import com.example.sport_be.entity.*;
import com.example.sport_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Update with frontend URL for production
public class UserController {
    private final UserService userService;

    // --- Category ---
    @GetMapping("/categories")
    public ResponseEntity<List<DanhMuc>> getAllCategories() {
        return ResponseEntity.ok(userService.getAllCategories());
    }

    // --- Product ---
    @GetMapping("/products")
    public ResponseEntity<List<SanPham>> getAllProducts() {
        return ResponseEntity.ok(userService.getAllProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<SanPham> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getProductById(id));
    }

    @GetMapping("/products/{id}/variants")
    public ResponseEntity<List<SanPhamChiTiet>> getProductVariants(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getProductVariants(id));
    }

    // --- Cart (Mocked User for now) ---
    @GetMapping("/cart/{userId}")
    public ResponseEntity<List<GioHangChiTiet>> getCartItems(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getCartItems(userId));
    }

    @PostMapping("/cart/{userId}/add")
    public ResponseEntity<String> addToCart(@PathVariable Integer userId, @RequestParam Integer spctId, @RequestParam Integer quantity) {
        userService.addToCart(userId, spctId, quantity);
        return ResponseEntity.ok("Added to cart");
    }

    // --- Order ---
    @PostMapping("/order/create")
    public ResponseEntity<HoaDon> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(userService.createOrder(request));
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<List<HoaDon>> getUserOrders(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getUserOrders(userId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<HoaDon> getOrderById(@PathVariable Integer orderId) {
        return ResponseEntity.ok(userService.getOrderById(orderId));
    }

    // --- Voucher ---
    @GetMapping("/vouchers")
    public ResponseEntity<List<MaGiamGia>> getAllVouchers() {
        return ResponseEntity.ok(userService.getAllVouchers());
    }
}
