package com.example.sport_be.service;

import com.example.sport_be.dto.CheckoutResponse;
import com.example.sport_be.dto.OrderRequest;
import com.example.sport_be.dto.OrderResponse;
import com.example.sport_be.dto.ProductResponse;
import com.example.sport_be.entity.*;
import com.example.sport_be.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final ThuongHieuRepository thuongHieuRepository;
    private final MaGiamGiaRepository maGiamGiaRepository;
    private final PtThanhToanRepository ptThanhToanRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository; // Đưa lên đầu
    private final VNPayService vnpayService;
    private final EntityManager entityManager;

    // --- Category ---
    public List<DanhMuc> getAllCategories() {
        return danhMucRepository.findByTrangThaiTrue();
    }

    // --- Brand ---
    public List<ThuongHieu> getAllBrands() {
        return thuongHieuRepository.findByTrangThaiTrue();
    }

    // --- Payment Method ---
    public List<PtThanhToan> getAllPaymentMethods() {
        return ptThanhToanRepository.findAll();
    }

    // --- Product ---
    public List<ProductResponse> getAllProducts() {
        List<SanPham> products = sanPhamRepository.findAll();
        List<ProductResponse> responses = new ArrayList<>();
        for (SanPham p : products) {
            responses.add(convertToResponse(p));
        }
        return responses;
    }

    public ProductResponse getProductById(Integer id) {
        SanPham p = sanPhamRepository.findById(id).orElse(null);
        return p != null ? convertToResponse(p) : null;
    }

    private ProductResponse convertToResponse(SanPham p) {
        List<SanPhamChiTiet> variants = sanPhamChiTietRepository.findBySanPhamId(p.getId());
        BigDecimal minPrice = variants.stream()
                .map(SanPhamChiTiet::getGiaBan)
                .min(BigDecimal::compareTo)
                .orElse(p.getGiaGoc());

        return ProductResponse.builder()
                .id(p.getId())
                .ma(p.getMa())
                .tenSanPham(p.getTenSanPham())
                .danhMuc(p.getDanhMuc())
                .thuongHieu(p.getThuongHieu())
                .giaGoc(p.getGiaGoc())
                .giaBanMin(minPrice)
                .trangThai(p.getTrangThai())
                .hinhAnhs(p.getHinhAnhs())
                .build();
    }

    public List<SanPhamChiTiet> getProductVariants(Integer productId) {
        return sanPhamChiTietRepository.findBySanPhamId(productId);
    }

    // --- Cart ---
    public GioHang getOrCreateCart(Integer userId) {
        return gioHangRepository.findByNguoiDungIdAndTrangThaiAndLoaiGioHang(userId, "DANG_SU_DUNG", "ONLINE")
                .orElseGet(() -> {
                    NguoiDung user = nguoiDungRepository.findById(userId).orElse(null);
                    if (user == null) {
                        return null;
                    }
                    GioHang newCart = new GioHang();
                    newCart.setNguoiDung(user);
                    newCart.setLoaiGioHang("ONLINE");
                    newCart.setTrangThai("DANG_SU_DUNG");
                    return gioHangRepository.save(newCart);
                });
    }

    @Transactional
    public void addToCart(Integer userId, Integer spctId, Integer quantity) {
        GioHang cart = getOrCreateCart(userId);
        if (cart == null) {
            throw new RuntimeException("Người dùng không tồn tại hoặc không hợp lệ");
        }
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(spctId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        
        // Sử dụng native insert để kích hoạt trigger trg_cart_insert xử lý UPSERT
        String ma = "GHCT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        gioHangChiTietRepository.nativeAddToCart(cart.getId(), spctId, quantity, spct.getGiaBan(), ma);
    }

    @Transactional
    public void removeFromCart(Integer userId, Integer ghctId) {
        GioHang cart = getOrCreateCart(userId);
        if (cart == null) {
            throw new RuntimeException("Người dùng không tồn tại hoặc không hợp lệ");
        }
        GioHangChiTiet item = gioHangChiTietRepository.findById(ghctId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        if (!item.getGioHang().getId().equals(cart.getId())) {
            throw new RuntimeException("Invalid item for this user's cart");
        }

        gioHangChiTietRepository.delete(item);
    }

    public List<GioHangChiTiet> getCartItems(Integer userId) {
        GioHang cart = getOrCreateCart(userId);
        if (cart == null) {
            return new ArrayList<>();
        }
        return gioHangChiTietRepository.findByGioHangId(cart.getId());
    }

    // --- Checkout & Order ---

    /**
     * Chức năng Thanh toán (Checkout)
     * Tính toán thông tin đơn hàng trước khi đặt
     */
    public CheckoutResponse getCheckoutInfo(OrderRequest request) {
        if (request.getCartItemIds() == null || request.getCartItemIds().isEmpty()) {
            return CheckoutResponse.builder()
                    .tongTienHang(BigDecimal.ZERO)
                    .tienGiam(BigDecimal.ZERO)
                    .phiVanChuyen(BigDecimal.valueOf(30000))
                    .tongThanhToan(BigDecimal.valueOf(30000))
                    .items(new ArrayList<>())
                    .build();
        }
        
        BigDecimal tongTienHang = gioHangChiTietRepository.calculateTotal(request.getCartItemIds());
        if (tongTienHang == null) tongTienHang = BigDecimal.ZERO;
        
        List<CheckoutResponse.CartItemInfo> items = new ArrayList<>();
        
        for (Integer ghctId : request.getCartItemIds()) {
            GioHangChiTiet ghct = gioHangChiTietRepository.findById(ghctId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm trong giỏ không tồn tại: ID=" + ghctId));
            SanPhamChiTiet spct = ghct.getSanPhamChiTiet();
            BigDecimal donGia = ghct.getDonGia() != null ? ghct.getDonGia() : spct.getGiaBan();
            BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(ghct.getSoLuong()));
            
            items.add(CheckoutResponse.CartItemInfo.builder()
                    .spctId(spct.getId())
                    .tenSanPham(spct.getSanPham().getTenSanPham())
                    .kichThuoc(spct.getKichThuoc() != null ? spct.getKichThuoc().getTen() : "N/A")
                    .mauSac(spct.getMauSac() != null ? spct.getMauSac().getTen() : "N/A")
                    .chatLieu(spct.getChatLieu() != null ? spct.getChatLieu().getTen() : "N/A")
                    .donGia(donGia)
                    .soLuong(ghct.getSoLuong())
                    .thanhTien(thanhTien)
                    .build());
        }
        
        BigDecimal tienGiam = BigDecimal.ZERO;
        if (request.getVoucherId() != null) {
            MaGiamGia voucher = maGiamGiaRepository.findById(request.getVoucherId()).orElse(null);
            if (voucher != null) {
                if ("PERCENT".equals(voucher.getKieuGiamGia())) {
                    tienGiam = tongTienHang.multiply(voucher.getGiaTriGiam()).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                    if (voucher.getGiaTriGiamToiDa() != null && tienGiam.compareTo(voucher.getGiaTriGiamToiDa()) > 0) {
                        tienGiam = voucher.getGiaTriGiamToiDa();
                    }
                } else {
                    tienGiam = voucher.getGiaTriGiam() != null ? voucher.getGiaTriGiam() : BigDecimal.ZERO;
                }
            }
        }
        
        BigDecimal phiVanChuyen = BigDecimal.valueOf(30000); // Giá vận chuyển cố định hoặc tính toán
        
        return CheckoutResponse.builder()
                .tongTienHang(tongTienHang)
                .tienGiam(tienGiam)
                .phiVanChuyen(phiVanChuyen)
                .tongThanhToan(tongTienHang.subtract(tienGiam).add(phiVanChuyen))
                .items(items)
                .build();
    }

    /**
     * Chức năng Đặt hàng (Place Order)
     * Lưu đơn hàng và chi tiết, dựa vào trigger trg_xu_ly_hoa_don_chi_tiet để xử lý tồn kho và tổng tiền
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request, HttpServletRequest httpRequest) {
        if (request.getUserId() == null) {
            throw new RuntimeException("User ID is required");
        }
        NguoiDung user = nguoiDungRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        
        PtThanhToan ptThanhToan = ptThanhToanRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Phương thức thanh toán không tồn tại"));

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon("HD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        hoaDon.setNguoiDung(user);
        hoaDon.setPtThanhToan(ptThanhToan);
        hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());
        hoaDon.setSoDienThoai(request.getSoDienThoai());
        hoaDon.setTinh(request.getTinh());
        hoaDon.setHuyen(request.getHuyen());
        hoaDon.setXa(request.getXa());
        hoaDon.setDiaChiChiTiet(request.getDiaChiChiTiet());
        hoaDon.setGhiChu(request.getGhiChu());
        hoaDon.setPhiVanChuyen(BigDecimal.valueOf(30000));
        hoaDon.setTrangThaiDon("CHO_XAC_NHAN");
        hoaDon.setLoaiDonHang("ONLINE");
        
        // Tính toán giảm giá từ voucher
        CheckoutResponse calc = getCheckoutInfo(request);
        hoaDon.setTienGiam(calc.getTienGiam());
        
        if (request.getVoucherId() != null) {
            MaGiamGia voucher = maGiamGiaRepository.findById(request.getVoucherId()).orElse(null);
            if (voucher != null) {
                hoaDon.setMaGiamGia(voucher);
                int usedCount = (voucher.getSoLuongDaDung() != null) ? voucher.getSoLuongDaDung() : 0;
                voucher.setSoLuongDaDung(usedCount + 1);
                maGiamGiaRepository.save(voucher);
            }
        }
        
        // Đặt giá trị mặc định, trigger sẽ cập nhật lại sau khi chèn hoa_don_chi_tiet
        hoaDon.setTongTienHang(BigDecimal.ZERO);
        hoaDon.setTongThanhToan(BigDecimal.ZERO);
        
        HoaDon savedOrder = hoaDonRepository.saveAndFlush(hoaDon);
        
        // Lưu lịch sử hóa đơn
        LichSuHoaDon history = new LichSuHoaDon();
        history.setHoaDon(savedOrder);
        history.setTrangThai("DA_DAT");
        lichSuHoaDonRepository.save(history);
        
        for (Integer ghctId : request.getCartItemIds()) {
            GioHangChiTiet ghct = gioHangChiTietRepository.findById(ghctId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm trong giỏ không tồn tại: ID=" + ghctId));
            SanPhamChiTiet spct = ghct.getSanPhamChiTiet();
            
            // Lấy giá bán từ sản phẩm chi tiết
            BigDecimal donGia = spct.getGiaBan();
            
            HoaDonChiTiet detail = new HoaDonChiTiet();
            detail.setMaHoaDonChiTiet("HDCT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            detail.setHoaDon(savedOrder);
            detail.setSanPhamChiTiet(spct);
            detail.setTenSanPham(spct.getSanPham() != null ? spct.getSanPham().getTenSanPham() : "N/A");
            detail.setKichThuoc(spct.getKichThuoc() != null ? spct.getKichThuoc().getTen() : "N/A");
            detail.setMauSac(spct.getMauSac() != null ? spct.getMauSac().getTen() : "N/A");
            detail.setChatLieu(spct.getChatLieu() != null ? spct.getChatLieu().getTen() : "N/A");
            detail.setDonGia(donGia);
            detail.setSoLuong(ghct.getSoLuong());
            detail.setThanhTien(donGia.multiply(BigDecimal.valueOf(ghct.getSoLuong())));

            // Trigger trg_update_tong_tien_hd sẽ tự động:
            // Cập nhật tong_tien_hang và tong_thanh_toan của hoaDon
            hoaDonChiTietRepository.save(detail);
            
            // Xóa khỏi giỏ hàng
            gioHangChiTietRepository.delete(ghct);
        }
        
        // Refresh để lấy dữ liệu đã được trigger cập nhật trong database
        entityManager.flush();
        savedOrder = hoaDonRepository.findById(savedOrder.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng sau khi tạo"));
        
        String paymentUrl = null;
        if ("VNPAY".equals(ptThanhToan.getMaPtThanhToan())) {
            BigDecimal totalToPay = savedOrder.getTongThanhToan() != null ? savedOrder.getTongThanhToan() : BigDecimal.ZERO;
            paymentUrl = vnpayService.createPaymentUrl(httpRequest, 
                totalToPay.longValue(), 
                "Thanh toan don hang " + savedOrder.getMaHoaDon(), 
                "http://localhost:8080/api/user/vnpay-callback");
        }
        
        return OrderResponse.builder()
                .hoaDon(savedOrder)
                .paymentUrl(paymentUrl)
                .build();
    }

    public List<HoaDon> getUserOrders(Integer userId) {
        return hoaDonRepository.findByNguoiDungIdOrderByNgayTaoDesc(userId);
    }

    public Object getOrderById(Integer orderId) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId).orElse(null);
        if (hoaDon == null) return null;
        
        List<HoaDonChiTiet> items = hoaDonChiTietRepository.findByHoaDonId(orderId);
        List<LichSuHoaDon> history = lichSuHoaDonRepository.findByHoaDonIdOrderByNgayCapNhatDesc(orderId);
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("bill", hoaDon);
        response.put("items", items);
        response.put("history", history);
        return response;
    }

    @Transactional
    public void cancelOrder(Integer orderId) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        
        // Cho phép hủy ở trạng thái Đã đặt, Chờ xác nhận hoặc Đã xác nhận
        if (!"DA_DAT".equals(hoaDon.getTrangThaiDon()) && !"CHO_XAC_NHAN".equals(hoaDon.getTrangThaiDon()) && !"DA_XAC_NHAN".equals(hoaDon.getTrangThaiDon())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng ở trạng thái Đã đặt, Chờ xác nhận hoặc Đã xác nhận");
        }
        
        hoaDon.setTrangThaiDon("DA_HUY");
        hoaDonRepository.save(hoaDon);
        
        // Log history
        LichSuHoaDon history = new LichSuHoaDon();
        history.setHoaDon(hoaDon);
        history.setTrangThai("DA_HUY");
        lichSuHoaDonRepository.save(history);
    }

    public List<MaGiamGia> getAllVouchers() {
        return maGiamGiaRepository.findByTrangThaiTrue();
    }

    @Transactional
    public NguoiDung updateProfile(Integer userId, NguoiDung updatedUser) {
        NguoiDung user = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        
        user.setHoTen(updatedUser.getHoTen());
        user.setSoDienThoai(updatedUser.getSoDienThoai());
        // Email thường không cho đổi hoặc cần verify phức tạp hơn, ở đây giữ nguyên
        
        return nguoiDungRepository.save(user);
    }

    @Transactional
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        NguoiDung user = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        
        if (!user.getMatKhau().equals(oldPassword)) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }
        
        user.setMatKhau(newPassword);
        nguoiDungRepository.save(user);
    }
}
