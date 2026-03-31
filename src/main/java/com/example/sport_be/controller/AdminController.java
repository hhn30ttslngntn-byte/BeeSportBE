package com.example.sport_be.controller;

import com.example.sport_be.entity.*;
import com.example.sport_be.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    private final AdminService adminService;

    // --- Product ---
    @GetMapping("/products")
    public ResponseEntity<List<SanPham>> getAllProducts() {
        return ResponseEntity.ok(adminService.getAllProducts());
    }

    @PostMapping("/products")
    public ResponseEntity<SanPham> saveProduct(@RequestBody SanPham sanPham) {
        return ResponseEntity.ok(adminService.saveProduct(sanPham));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        adminService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    // --- Category ---
    @GetMapping("/categories")
    public ResponseEntity<List<DanhMuc>> getAllCategories() {
        return ResponseEntity.ok(adminService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<DanhMuc> saveCategory(@RequestBody DanhMuc danhMuc) {
        return ResponseEntity.ok(adminService.saveCategory(danhMuc));
    }

    // --- User ---
    @GetMapping("/users")
    public ResponseEntity<List<NguoiDung>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/role/{roleId}")
    public ResponseEntity<List<NguoiDung>> getUsersByRole(@PathVariable Integer roleId) {
        return ResponseEntity.ok(adminService.getUsersByRole(roleId));
    }

    // --- Bill ---
    @GetMapping("/bills")
    public ResponseEntity<List<HoaDon>> getAllBills() {
        return ResponseEntity.ok(adminService.getAllBills());
    }

    // --- Voucher ---
    @GetMapping("/vouchers")
    public ResponseEntity<List<MaGiamGia>> getAllVouchers() {
        return ResponseEntity.ok(adminService.getAllVouchers());
    }

    // --- Attributes ---
    @GetMapping("/colors")
    public ResponseEntity<List<MauSac>> getAllColors() { return ResponseEntity.ok(adminService.getAllColors()); }
    @GetMapping("/sizes")
    public ResponseEntity<List<KichThuoc>> getAllSizes() { return ResponseEntity.ok(adminService.getAllSizes()); }
    @GetMapping("/materials")
    public ResponseEntity<List<ChatLieu>> getAllMaterials() { return ResponseEntity.ok(adminService.getAllMaterials()); }
}
