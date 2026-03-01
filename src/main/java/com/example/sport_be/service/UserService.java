package com.example.sport_be.service;

import com.example.sport_be.entity.*;
import com.example.sport_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final SanPhamRepository sanPhamRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final GioHangRepository gioHangRepository;
    private final GioHangChiTietRepository gioHangChiTietRepository;
    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final DanhMucRepository danhMucRepository;

    // --- Category ---
    public List<DanhMuc> getAllCategories() {
        return danhMucRepository.findByTrangThaiTrue();
    }

    // --- Product ---
    public List<SanPham> getAllProducts() {
        return sanPhamRepository.findByTrangThaiTrue();
    }

    public SanPham getProductById(Integer id) {
        return sanPhamRepository.findById(id).orElse(null);
    }

    public List<SanPhamChiTiet> getProductVariants(Integer productId) {
        return sanPhamChiTietRepository.findBySanPhamId(productId);
    }

    // --- Cart ---
    public GioHang getOrCreateCart(Integer userId) {
        return gioHangRepository.findByNguoiDungIdAndTrangThai(userId, "DANG_SU_DUNG")
                .orElseGet(() -> {
                    GioHang newCart = new GioHang();
                    newCart.setNguoiDung(nguoiDungRepository.findById(userId).orElse(null));
                    return gioHangRepository.save(newCart);
                });
    }

    @Transactional
    public void addToCart(Integer userId, Integer spctId, Integer quantity) {
        GioHang cart = getOrCreateCart(userId);
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(cart.getId());
        
        Optional<GioHangChiTiet> existingItem = items.stream()
                .filter(item -> item.getSanPhamChiTiet().getId().equals(spctId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setSoLuong(existingItem.get().getSoLuong() + quantity);
            gioHangChiTietRepository.save(existingItem.get());
        } else {
            GioHangChiTiet newItem = new GioHangChiTiet();
            newItem.setGioHang(cart);
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(spctId).orElseThrow();
            newItem.setSanPhamChiTiet(spct);
            newItem.setSoLuong(quantity);
            gioHangChiTietRepository.save(newItem);
        }
    }

    public List<GioHangChiTiet> getCartItems(Integer userId) {
        GioHang cart = getOrCreateCart(userId);
        return gioHangChiTietRepository.findByGioHangId(cart.getId());
    }

    // --- Order ---
    public List<HoaDon> getUserOrders(Integer userId) {
        return hoaDonRepository.findByNguoiDungId(userId);
    }

    public HoaDon getOrderById(Integer orderId) {
        return hoaDonRepository.findById(orderId).orElse(null);
    }
}
