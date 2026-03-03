package com.example.sport_be.service;

import com.example.sport_be.dto.OrderRequest;
import com.example.sport_be.entity.*;
import com.example.sport_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private final MaGiamGiaRepository maGiamGiaRepository;

    // --- Category ---
    public List<DanhMuc> getAllCategories() {
        return danhMucRepository.findByTrangThaiTrue();
    }

    // --- Product ---
    public List<SanPham> getAllProducts() {
        return sanPhamRepository.findAll();
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
    @Transactional
    public HoaDon createOrder(OrderRequest request) {
        NguoiDung user = nguoiDungRepository.findById(request.getUserId()).orElseThrow();
        
        HoaDon hoaDon = new HoaDon();
        hoaDon.setNguoiDung(user);
        hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());
        hoaDon.setSoDienThoai(request.getSoDienThoai());
        hoaDon.setTinh(request.getTinh());
        hoaDon.setHuyen(request.getHuyen());
        hoaDon.setXa(request.getXa());
        hoaDon.setDiaChiChiTiet(request.getDiaChiChiTiet());
        hoaDon.setGhiChu(request.getGhiChu());
        
        BigDecimal tongTienHang = BigDecimal.ZERO;
        List<HoaDonChiTiet> details = new ArrayList<>();
        
        for (Integer ghctId : request.getCartItemIds()) {
            GioHangChiTiet ghct = gioHangChiTietRepository.findById(ghctId).orElseThrow();
            SanPhamChiTiet spct = ghct.getSanPhamChiTiet();
            
            HoaDonChiTiet detail = new HoaDonChiTiet();
            detail.setHoaDon(hoaDon);
            detail.setSanPhamChiTiet(spct);
            detail.setTenSanPham(spct.getSanPham().getTen());
            detail.setKichThuoc(spct.getKichThuoc().getTen());
            detail.setMauSac(spct.getMauSac().getTen());
            detail.setChatLieu(spct.getChatLieu().getTen());
            detail.setDonGia(spct.getGiaBan());
            detail.setSoLuong(ghct.getSoLuong());
            detail.setThanhTien(spct.getGiaBan().multiply(BigDecimal.valueOf(ghct.getSoLuong())));
            
            tongTienHang = tongTienHang.add(detail.getThanhTien());
            details.add(detail);
            
            // Deduct inventory
            if (spct.getSoLuong() < ghct.getSoLuong()) {
                throw new RuntimeException("Sản phẩm " + spct.getSanPham().getTen() + " (" + spct.getKichThuoc().getTen() + ") không đủ hàng");
            }
            spct.setSoLuong(spct.getSoLuong() - ghct.getSoLuong());
            sanPhamChiTietRepository.save(spct);
            
            // Remove from cart
            gioHangChiTietRepository.delete(ghct);
        }
        
        hoaDon.setTongTienHang(tongTienHang);
        
        BigDecimal tienGiam = BigDecimal.ZERO;
        if (request.getVoucherId() != null) {
            MaGiamGia voucher = maGiamGiaRepository.findById(request.getVoucherId()).orElseThrow();
            hoaDon.setMaGiamGia(voucher);
            
            if ("PERCENT".equals(voucher.getKieuGiamGia())) {
                tienGiam = tongTienHang.multiply(voucher.getGiaTriGiam()).divide(BigDecimal.valueOf(100));
                if (voucher.getGiaTriGiamToiDa() != null && tienGiam.compareTo(voucher.getGiaTriGiamToiDa()) > 0) {
                    tienGiam = voucher.getGiaTriGiamToiDa();
                }
            } else {
                tienGiam = voucher.getGiaTriGiam();
            }
            voucher.setSoLuongDaDung(voucher.getSoLuongDaDung() + 1);
            maGiamGiaRepository.save(voucher);
        }
        
        hoaDon.setTienGiam(tienGiam);
        hoaDon.setTongThanhToan(tongTienHang.subtract(tienGiam).add(hoaDon.getPhiVanChuyen()));
        
        HoaDon savedOrder = hoaDonRepository.save(hoaDon);
        for (HoaDonChiTiet detail : details) {
            detail.setHoaDon(savedOrder);
            hoaDonChiTietRepository.save(detail);
        }
        
        return savedOrder;
    }

    public List<HoaDon> getUserOrders(Integer userId) {
        return hoaDonRepository.findByNguoiDungId(userId);
    }

    public HoaDon getOrderById(Integer orderId) {
        return hoaDonRepository.findById(orderId).orElse(null);
    }

    public List<MaGiamGia> getAllVouchers() {
        return maGiamGiaRepository.findByTrangThaiTrue();
    }
}
