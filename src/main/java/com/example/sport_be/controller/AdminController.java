package com.example.sport_be.controller;

import com.example.sport_be.entity.*;
import com.example.sport_be.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    private final AdminService adminService;
    private final HttpServletRequest httpServletRequest;

    // --- Product ---
    @GetMapping("/products")
    public ResponseEntity<List<SanPham>> getAllProducts() {
        return ResponseEntity.ok(adminService.getAllProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.getProductDetail(id));
    }

    @PostMapping("/products")
    public ResponseEntity<SanPham> saveProduct(@RequestBody com.example.sport_be.dto.ProductSaveRequest request) {
        return ResponseEntity.ok(adminService.saveProductWithVariants(request.getProduct(), request.getVariants(), request.getImageUrls()));
    }

    @PostMapping("/products/upload-images")
    public ResponseEntity<List<String>> uploadProductImages(@RequestParam("files") List<MultipartFile> files) {
        String baseUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort();
        return ResponseEntity.ok(adminService.uploadProductImages(files, baseUrl));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        adminService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/products/{id}/toggle-status")
    public ResponseEntity<Void> toggleProductStatus(@PathVariable Integer id) {
        adminService.toggleProductStatus(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/product-details/bulk")
    public ResponseEntity<List<SanPhamChiTiet>> saveProductDetails(@RequestBody List<SanPhamChiTiet> details) {
        return ResponseEntity.ok(adminService.saveProductDetails(details));
    }

    // --- Category ---
    @GetMapping("/categories")
    public ResponseEntity<List<DanhMuc>> getAllCategories() {
        return ResponseEntity.ok(adminService.getAllCategories());
    }

    @GetMapping("/brands")
    public ResponseEntity<List<ThuongHieu>> getAllBrands() {
        return ResponseEntity.ok(adminService.getAllBrands());
    }

    @PostMapping("/brands")
    public ResponseEntity<ThuongHieu> saveBrand(@RequestBody ThuongHieu brand) {
        return ResponseEntity.ok(adminService.saveBrand(brand));
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

    @PostMapping("/users")
    public ResponseEntity<NguoiDung> saveUser(@RequestBody NguoiDung user) {
        return ResponseEntity.ok(adminService.saveUser(user));
    }

    // --- Bill ---
    @GetMapping("/bills")
    public ResponseEntity<Object> getAllBills() {
        return ResponseEntity.ok(adminService.getAllBills());
    }

    @PutMapping("/bills/{id}/status")
    public ResponseEntity<HoaDon> updateBillStatus(@PathVariable Integer id, @RequestBody String status) {
        return ResponseEntity.ok(adminService.updateBillStatus(id, status));
    }

    @GetMapping("/bills/{id}")
    public ResponseEntity<Object> getBillDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.getBillDetail(id));
    }

    @GetMapping("/statistics")
    public ResponseEntity<Object> getDashboardStatistics(
            @RequestParam(defaultValue = "day") String mode,
            @RequestParam(defaultValue = "quantity") String metric,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(adminService.getDashboardStatistics(mode, metric, from, to));
    }

    // --- Voucher ---
    @GetMapping("/vouchers")
    public ResponseEntity<List<MaGiamGia>> getAllVouchers() {
        return ResponseEntity.ok(adminService.getAllVouchers());
    }

    @GetMapping("/vouchers/{id}")
    public ResponseEntity<MaGiamGia> getVoucherById(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.getVoucherById(id));
    }

    @PostMapping("/vouchers")
    public ResponseEntity<MaGiamGia> saveVoucher(@RequestBody MaGiamGia voucher) {
        return ResponseEntity.ok(adminService.saveVoucher(voucher));
    }

    @DeleteMapping("/vouchers/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Integer id) {
        adminService.deleteVoucher(id);
        return ResponseEntity.ok().build();
    }

    // --- Promotion (DotGiamGia) ---
    @GetMapping("/promotions")
    public ResponseEntity<List<DotGiamGia>> getAllPromotions() {
        return ResponseEntity.ok(adminService.getAllPromotions());
    }

    @GetMapping("/promotions/{id}")
    public ResponseEntity<DotGiamGia> getPromotionById(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.getPromotionById(id));
    }

    @GetMapping("/promotions/{id}/products")
    public ResponseEntity<List<Integer>> getProductIdsForPromotion(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.getProductIdsForPromotion(id));
    }

    @PostMapping("/promotions")
    public ResponseEntity<DotGiamGia> savePromotion(@RequestBody com.example.sport_be.dto.PromotionSaveRequest request) {
        return ResponseEntity.ok(adminService.savePromotion(request.getPromotion(), request.getProductIds()));
    }

    @DeleteMapping("/promotions/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Integer id) {
        adminService.deletePromotion(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<List<PtThanhToan>> getAllPaymentMethods() {
        return ResponseEntity.ok(adminService.getAllPaymentMethods());
    }

    // --- POS ---
    @GetMapping("/pos/invoices")
    public ResponseEntity<List<GioHang>> getWaitingInvoices() {
        return ResponseEntity.ok(adminService.getWaitingInvoices());
    }

    @GetMapping("/pos/invoices/{id}")
    public ResponseEntity<Object> getWaitingInvoiceDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.getWaitingInvoiceDetail(id));
    }

    @PostMapping("/pos/invoices")
    public ResponseEntity<GioHang> createWaitingInvoice() {
        return ResponseEntity.ok(adminService.createWaitingInvoice());
    }

    @PostMapping("/pos/invoices/{id}/details")
    public ResponseEntity<Void> addInvoiceDetail(@PathVariable Integer id, @RequestParam Integer spctId, @RequestParam Integer quantity) {
        adminService.addInvoiceDetail(id, spctId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/pos/details/{detailId}")
    public ResponseEntity<Void> removeInvoiceDetail(@PathVariable Integer detailId) {
        adminService.removeInvoiceDetail(detailId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/pos/details/{detailId}/quantity")
    public ResponseEntity<Void> updateInvoiceQuantity(@PathVariable Integer detailId, @RequestParam Integer quantity) {
        adminService.updateInvoiceQuantity(detailId, quantity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/pos/invoices/{id}/customer")
    public ResponseEntity<Void> updateInvoiceCustomer(@PathVariable Integer id, @RequestParam Integer customerId) {
        adminService.updateInvoiceCustomer(id, customerId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/pos/invoices/{id}/voucher")
    public ResponseEntity<Void> applyVoucher(@PathVariable Integer id, @RequestParam String voucherCode) {
        adminService.applyVoucher(id, voucherCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pos/invoices/{id}/applicable-vouchers")
    public ResponseEntity<List<MaGiamGia>> getApplicableVouchersForInvoice(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.getApplicableVouchersForInvoice(id));
    }

    @PostMapping("/pos/invoices/{id}/checkout")
    public ResponseEntity<Void> checkoutPOS(@PathVariable Integer id, @RequestParam Integer paymentMethodId, @RequestBody(required = false) String note, @RequestParam(required = false) Integer customerId, @RequestParam(required = false) String voucherCode) {
        adminService.checkoutPOS(id, paymentMethodId, note, customerId, voucherCode);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/pos/invoices/{id}")
    public ResponseEntity<Void> deleteWaitingInvoice(@PathVariable Integer id) {
        adminService.deleteWaitingInvoice(id);
        return ResponseEntity.ok().build();
    }

    // --- Attributes ---
    @GetMapping("/colors")
    public ResponseEntity<List<MauSac>> getAllColors() { return ResponseEntity.ok(adminService.getAllColors()); }
    @GetMapping("/sizes")
    public ResponseEntity<List<KichThuoc>> getAllSizes() { return ResponseEntity.ok(adminService.getAllSizes()); }
    @GetMapping("/materials")
    public ResponseEntity<List<ChatLieu>> getAllMaterials() { return ResponseEntity.ok(adminService.getAllMaterials()); }

    @PostMapping("/colors")
    public ResponseEntity<MauSac> saveColor(@RequestBody MauSac color) { return ResponseEntity.ok(adminService.saveColor(color)); }
    @PostMapping("/sizes")
    public ResponseEntity<KichThuoc> saveSize(@RequestBody KichThuoc size) { return ResponseEntity.ok(adminService.saveSize(size)); }
    @PostMapping("/materials")
    public ResponseEntity<ChatLieu> saveMaterial(@RequestBody ChatLieu material) { return ResponseEntity.ok(adminService.saveMaterial(material)); }
}
