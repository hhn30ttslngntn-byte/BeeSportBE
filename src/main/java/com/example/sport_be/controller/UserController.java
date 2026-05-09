package com.example.sport_be.controller;

import com.example.sport_be.dto.AddressRequest;
import com.example.sport_be.dto.AddressResponse;
import com.example.sport_be.dto.CheckoutResponse;
import com.example.sport_be.dto.OrderRequest;
import com.example.sport_be.dto.OrderResponse;
import com.example.sport_be.dto.ProductResponse;
import com.example.sport_be.entity.*;
import com.example.sport_be.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final HttpServletRequest httpServletRequest;

    @GetMapping("/categories")
    public ResponseEntity<List<DanhMuc>> getAllCategories() {
        return ResponseEntity.ok(userService.getAllCategories());
    }

    @GetMapping("/brands")
    public ResponseEntity<List<ThuongHieu>> getAllBrands() {
        return ResponseEntity.ok(userService.getAllBrands());
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<List<PtThanhToan>> getAllPaymentMethods() {
        return ResponseEntity.ok(userService.getAllPaymentMethods());
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(userService.getAllProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getProductById(id));
    }

    @GetMapping("/products/{id}/variants")
    public ResponseEntity<List<SanPhamChiTiet>> getProductVariants(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getProductVariants(id));
    }

    @GetMapping("/wishlist/{userId}")
    public ResponseEntity<List<ProductResponse>> getWishlist(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getWishlist(userId));
    }

    @PostMapping("/wishlist/{userId}/toggle")
    public ResponseEntity<Boolean> toggleWishlist(@PathVariable Integer userId, @RequestParam Integer productId) {
        return ResponseEntity.ok(userService.toggleWishlist(userId, productId));
    }

    @DeleteMapping("/wishlist/{userId}/{productId}")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Integer userId, @PathVariable Integer productId) {
        userService.removeFromWishlist(userId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cart/{userId}")
    public ResponseEntity<List<GioHangChiTiet>> getCartItems(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getCartItems(userId));
    }

    @PostMapping("/cart/{userId}/add")
    public ResponseEntity<String> addToCart(
            @PathVariable Integer userId,
            @RequestParam Integer spctId,
            @RequestParam Integer quantity
    ) {
        try {
            userService.addToCart(userId, spctId, quantity);
            return ResponseEntity.ok("Added to cart");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/cart/{userId}/remove/{ghctId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Integer userId, @PathVariable Integer ghctId) {
        userService.removeFromCart(userId, ghctId);
        return ResponseEntity.ok("Removed from cart");
    }

    @PostMapping("/order/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(userService.getCheckoutInfo(request));
    }

    @PostMapping("/order/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(userService.createOrder(request, httpServletRequest));
    }

    @GetMapping("/vnpay-callback")
    public void vnpayCallback(
            @RequestParam Map<String, String> params,
            jakarta.servlet.http.HttpServletResponse response
    ) throws IOException {
        response.setHeader("ngrok-skip-browser-warning", "true");
        response.sendRedirect(userService.handleVNPayCallback(params));
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<List<HoaDon>> getUserOrders(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getUserOrders(userId));
    }

    @GetMapping("/order/{orderId:[0-9]+}")
    public ResponseEntity<Object> getOrderById(@PathVariable Integer orderId) {
        return ResponseEntity.ok(userService.getOrderById(orderId));
    }

    @PostMapping("/order/{orderId:[0-9]+}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Integer orderId) {
        try {
            userService.cancelOrder(orderId);
            return ResponseEntity.ok("Da huy don hang thanh cong");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/order/{orderId:[0-9]+}/confirm-received")
    public ResponseEntity<String> confirmReceived(@PathVariable Integer orderId) {
        try {
            userService.confirmReceived(orderId);
            return ResponseEntity.ok("Đã xác nhận nhận hàng thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/vouchers")
    public ResponseEntity<List<MaGiamGia>> getAllVouchers() {
        return ResponseEntity.ok(userService.getAllVouchers());
    }

    @GetMapping("/addresses/{userId}")
    public ResponseEntity<List<AddressResponse>> getUserAddresses(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getUserAddresses(userId));
    }

    @GetMapping("/regions/tinh")
    public ResponseEntity<List<Tinh>> getAllTinh() {
        return ResponseEntity.ok(userService.getAllTinh());
    }

    @GetMapping("/regions/huyen/{tinhId}")
    public ResponseEntity<List<Huyen>> getHuyenByTinh(@PathVariable Integer tinhId) {
        return ResponseEntity.ok(userService.getHuyenByTinh(tinhId));
    }

    @GetMapping("/regions/xa/{huyenId}")
    public ResponseEntity<List<Xa>> getXaByHuyen(@PathVariable Integer huyenId) {
        return ResponseEntity.ok(userService.getXaByHuyen(huyenId));
    }

    @PostMapping("/addresses/{userId}")
    public ResponseEntity<AddressResponse> saveAddress(@PathVariable Integer userId, @RequestBody AddressRequest request) {
        return ResponseEntity.ok(userService.saveAddress(userId, request));
    }

    @DeleteMapping("/addresses/{userId}/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer userId, @PathVariable Integer addressId) {
        userService.deleteAddress(userId, addressId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/addresses/{userId}/{addressId}/default")
    public ResponseEntity<Void> setDefaultAddress(@PathVariable Integer userId, @PathVariable Integer addressId) {
        userService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Integer userId, @RequestBody NguoiDung user) {
        try {
            return ResponseEntity.ok(userService.updateProfile(userId, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password/{userId}")
    public ResponseEntity<?> changePassword(@PathVariable Integer userId, @RequestBody Map<String, String> passwords) {
        try {
            userService.changePassword(userId, passwords.get("oldPassword"), passwords.get("newPassword"));
            return ResponseEntity.ok("Doi mat khau thanh cong");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // --- Đổi Trả ---
    @PostMapping("/doi-tra/request")
    public ResponseEntity<?> createDoiTraRequest(
            @RequestPart("data") com.example.sport_be.dto.DoiTraRequest request,
            @RequestPart("files") MultipartFile[] files
    ) {
        try {
            String baseUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort();
            return ResponseEntity.ok(userService.createDoiTraRequest(request, files, baseUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
