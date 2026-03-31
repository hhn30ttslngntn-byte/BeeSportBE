package com.example.sport_be.service;

import com.example.sport_be.dto.CheckoutResponse;
import com.example.sport_be.dto.AddressRequest;
import com.example.sport_be.dto.AddressResponse;
import com.example.sport_be.dto.OrderRequest;
import com.example.sport_be.dto.OrderResponse;
import com.example.sport_be.dto.ProductResponse;
import com.example.sport_be.entity.*;
import com.example.sport_be.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Value("${app.backend-public-url:http://localhost:8080}")
    private String backendPublicUrl;

    @Value("${app.frontend-url:http://localhost:5174}")
    private String frontendUrl;

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
    private final DiaChiVanChuyenRepository diaChiVanChuyenRepository;
    private final TinhRepository tinhRepository;
    private final HuyenRepository huyenRepository;
    private final XaRepository xaRepository;
    private final PtThanhToanRepository ptThanhToanRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;
    private final LichSuThanhToanRepository lichSuThanhToanRepository;
    private final DotGiamGiaRepository dotGiamGiaRepository;
    private final GiamGiaSanPhamRepository giamGiaSanPhamRepository;
    private final SanPhamYeuThichRepository sanPhamYeuThichRepository;
    private final VNPayService vnpayService;
    private final EntityManager entityManager;
    
    private BigDecimal getPromotionPrice(SanPham sp) {
        BigDecimal giaGoc = sp.getGiaGoc();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        GiamGiaSanPham activeGG = giamGiaSanPhamRepository.findAll().stream()
            .filter(gg -> gg.getSanPham().getId().equals(sp.getId()))
            .filter(gg -> gg.getDotGiamGia().getTrangThai())
            .filter(gg -> gg.getDotGiamGia().getNgayBatDau().isBefore(now) && gg.getDotGiamGia().getNgayKetHuc().isAfter(now))
            .findFirst().orElse(null);
            
        if (activeGG != null) {
            DotGiamGia dgg = activeGG.getDotGiamGia();
            sp.setTenKhuyenMai(dgg.getTenDot());
            if ("PERCENT".equals(dgg.getKieuGiamGia())) {
                BigDecimal giam = giaGoc.multiply(dgg.getGiaTriGiam().divide(new BigDecimal(100)));
                return giaGoc.subtract(giam);
            } else {
                return giaGoc.subtract(dgg.getGiaTriGiam());
            }
        }
        return giaGoc;
    }

    private BigDecimal getPromotionPriceForVariant(SanPhamChiTiet spct) {
        BigDecimal giaBan = spct.getGiaBan();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        GiamGiaSanPham activeGG = giamGiaSanPhamRepository.findAll().stream()
            .filter(gg -> gg.getSanPham().getId().equals(spct.getSanPham().getId()))
            .filter(gg -> gg.getDotGiamGia().getTrangThai())
            .filter(gg -> gg.getDotGiamGia().getNgayBatDau().isBefore(now) && gg.getDotGiamGia().getNgayKetHuc().isAfter(now))
            .findFirst().orElse(null);
            
        if (activeGG != null) {
            DotGiamGia dgg = activeGG.getDotGiamGia();
            if ("PERCENT".equals(dgg.getKieuGiamGia())) {
                BigDecimal giam = giaBan.multiply(dgg.getGiaTriGiam().divide(new BigDecimal(100)));
                return giaBan.subtract(giam);
            } else {
                return giaBan.subtract(dgg.getGiaTriGiam());
            }
        }
        return giaBan;
    }

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
                .map(this::getPromotionPriceForVariant)
                .min(BigDecimal::compareTo)
                .orElse(getPromotionPrice(p));

        return ProductResponse.builder()
                .id(p.getId())
                .ma(p.getMa())
                .tenSanPham(p.getTenSanPham())
                .danhMuc(p.getDanhMuc())
                .thuongHieu(p.getThuongHieu())
                .giaGoc(p.getGiaGoc())
                .giaSauGiam(getPromotionPrice(p))
                .giaBanMin(minPrice)
                .trangThai(p.getTrangThai())
                .hinhAnhs(p.getHinhAnhs())
                .build();
    }

    public List<SanPhamChiTiet> getProductVariants(Integer productId) {
        return sanPhamChiTietRepository.findBySanPhamId(productId);
    }

    public List<ProductResponse> getWishlist(Integer userId) {
        return sanPhamYeuThichRepository.findByNguoiDungId(userId).stream()
                .map(SanPhamYeuThich::getSanPham)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean toggleWishlist(Integer userId, Integer productId) {
        NguoiDung user = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        SanPham product = sanPhamRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        Optional<SanPhamYeuThich> existing = sanPhamYeuThichRepository.findByNguoiDungIdAndSanPhamId(userId, productId);
        if (existing.isPresent()) {
            sanPhamYeuThichRepository.delete(existing.get());
            return false;
        }

        SanPhamYeuThich wishlistItem = new SanPhamYeuThich();
        wishlistItem.setNguoiDung(user);
        wishlistItem.setSanPham(product);
        sanPhamYeuThichRepository.save(wishlistItem);
        return true;
    }

    @Transactional
    public void removeFromWishlist(Integer userId, Integer productId) {
        sanPhamYeuThichRepository.deleteByNguoiDungIdAndSanPhamId(userId, productId);
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
        
        // Tính giá sau giảm nếu có khuyến mãi
        BigDecimal donGia = getPromotionPriceForVariant(spct);
        
        // Sử dụng native insert để kích hoạt trigger trg_cart_insert xử lý UPSERT
        String ma = "GHCT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        gioHangChiTietRepository.nativeAddToCart(cart.getId(), spctId, quantity, donGia, ma);
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
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(cart.getId());
        
        // Cập nhật giá khuyến mại cho từng item dựa trên SPCT
        for (GioHangChiTiet item : items) {
            if (item.getSanPhamChiTiet() != null) {
                // Lấy giá khuyến mãi thực tế cho biến thể SPCT này
                BigDecimal giaSauGiam = getPromotionPriceForVariant(item.getSanPhamChiTiet());
                item.setDonGia(giaSauGiam);
                
                // Đồng bộ cả giaSauGiam trong đối tượng SanPham để FE dễ dùng
                if (item.getSanPhamChiTiet().getSanPham() != null) {
                    item.getSanPhamChiTiet().getSanPham().setGiaSauGiam(getPromotionPrice(item.getSanPhamChiTiet().getSanPham()));
                }
            }
        }
        
        return items;
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
            // Lấy giá bán sau khi áp dụng khuyến mãi
            BigDecimal donGia = getPromotionPriceForVariant(spct);
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
        syncShippingAddressToAddressBook(user, request);
        
        // Lưu lịch sử hóa đơn
        LichSuHoaDon history = new LichSuHoaDon();
        history.setHoaDon(savedOrder);
        history.setTrangThai("DA_DAT");
        history.setGhiChu("VNPAY".equals(ptThanhToan.getMaPtThanhToan())
                ? "Don hang da tao, dang cho thanh toan VNPay"
                : "Don hang da duoc tao");
        lichSuHoaDonRepository.save(history);
        
        for (Integer ghctId : request.getCartItemIds()) {
            GioHangChiTiet ghct = gioHangChiTietRepository.findById(ghctId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm trong giỏ không tồn tại: ID=" + ghctId));
            SanPhamChiTiet spct = ghct.getSanPhamChiTiet();
            
            // Lấy giá bán sau khi áp dụng khuyến mãi
            BigDecimal donGia = getPromotionPriceForVariant(spct);
            
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
            BigDecimal totalToPay = calc.getTongThanhToan() != null
                    ? calc.getTongThanhToan()
                    : savedOrder.getTongThanhToan();

            LichSuThanhToan pendingPayment = new LichSuThanhToan();
            pendingPayment.setHoaDon(savedOrder);
            pendingPayment.setPtThanhToan(ptThanhToan);
            pendingPayment.setSoTien(totalToPay);
            pendingPayment.setTrangThaiThanhToan("CHO_THANH_TOAN");
            lichSuThanhToanRepository.save(pendingPayment);

            paymentUrl = vnpayService.createPaymentUrl(
                    httpRequest,
                    totalToPay,
                    "Thanh toan don hang " + savedOrder.getMaHoaDon(),
                    backendPublicUrl + "/api/user/vnpay-callback",
                    savedOrder.getId().toString()
            );
        }
        
        return OrderResponse.builder()
                .hoaDon(savedOrder)
                .paymentUrl(paymentUrl)
                .build();
    }

    @Transactional
    public String handleVNPayCallback(Map<String, String> params) {
        String frontendRedirectBase = frontendUrl + "/order-history";

        if (!vnpayService.verifyCallback(params)) {
            return frontendRedirectBase + "?payment=invalid";
        }

        String txnRef = params.get("vnp_TxnRef");
        if (txnRef == null || txnRef.isBlank()) {
            return frontendRedirectBase + "?payment=invalid";
        }

        Integer orderId;
        try {
            orderId = Integer.valueOf(txnRef);
        } catch (NumberFormatException ex) {
            return frontendRedirectBase + "?payment=invalid";
        }

        HoaDon hoaDon = hoaDonRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay don hang cho giao dich VNPAY"));

        PtThanhToan ptThanhToan = hoaDon.getPtThanhToan();
        boolean success = "00".equals(params.get("vnp_ResponseCode"));

        LichSuThanhToan paymentHistory = lichSuThanhToanRepository
                .findTopByHoaDonIdOrderByNgayThanhToanDesc(orderId)
                .orElseGet(LichSuThanhToan::new);
        paymentHistory.setHoaDon(hoaDon);
        paymentHistory.setPtThanhToan(ptThanhToan);
        paymentHistory.setSoTien(hoaDon.getTongThanhToan());
        paymentHistory.setTrangThaiThanhToan(success ? "DA_THANH_TOAN" : "THAT_BAI");
        lichSuThanhToanRepository.save(paymentHistory);

        if (success) {
            if (!"CHO_XAC_NHAN".equals(hoaDon.getTrangThaiDon())) {
                hoaDon.setTrangThaiDon("CHO_XAC_NHAN");
                hoaDonRepository.save(hoaDon);

                LichSuHoaDon history = new LichSuHoaDon();
                history.setHoaDon(hoaDon);
                history.setTrangThai("DA_DAT");
                history.setGhiChu("Thanh toan VNPAY thanh cong");
                lichSuHoaDonRepository.save(history);
            }
            return frontendRedirectBase + "?payment=success&orderId=" + hoaDon.getId();
        }

        if (!"DA_HUY".equals(hoaDon.getTrangThaiDon())) {
            hoaDon.setTrangThaiDon("DA_HUY");
            hoaDonRepository.save(hoaDon);

            LichSuHoaDon history = new LichSuHoaDon();
            history.setHoaDon(hoaDon);
            history.setTrangThai("DA_HUY");
            history.setGhiChu("Thanh toan VNPAY that bai hoac bi huy");
            lichSuHoaDonRepository.save(history);
        }

        return frontendRedirectBase + "?payment=failed&orderId=" + hoaDon.getId();
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

    public List<AddressResponse> getUserAddresses(Integer userId) {
        return diaChiVanChuyenRepository.findByNguoiDungIdAndTrangThaiTrueOrderByLaMacDinhDescIdDesc(userId)
                .stream()
                .map(this::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse saveAddress(Integer userId, AddressRequest request) {
        NguoiDung user = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        DiaChiVanChuyen address = request.getId() != null
                ? diaChiVanChuyenRepository.findByIdAndNguoiDungId(request.getId(), userId)
                    .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"))
                : new DiaChiVanChuyen();

        address.setNguoiDung(user);
        address.setTenNguoiNhan(request.getTenNguoiNhan());
        address.setSoDienThoai(request.getSoDienThoai());
        address.setDiaChiChiTiet(request.getDiaChiChiTiet());
        address.setLoaiDiaChi(request.getLoaiDiaChi());
        address.setTrangThai(true);
        address.setLaMacDinh(Boolean.TRUE.equals(request.getLaMacDinh()));
        if (request.getXaId() != null) {
            address.setXa(xaRepository.findById(request.getXaId()).orElse(null));
        } else {
            address.setXa(null);
        }

        if (Boolean.TRUE.equals(request.getLaMacDinh())) {
            clearDefaultAddress(userId);
        }

        DiaChiVanChuyen saved = diaChiVanChuyenRepository.save(address);
        return toAddressResponse(saved);
    }

    @Transactional
    public void deleteAddress(Integer userId, Integer addressId) {
        DiaChiVanChuyen address = diaChiVanChuyenRepository.findByIdAndNguoiDungId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
        address.setTrangThai(false);
        address.setLaMacDinh(false);
        diaChiVanChuyenRepository.save(address);
    }

    @Transactional
    public void setDefaultAddress(Integer userId, Integer addressId) {
        DiaChiVanChuyen address = diaChiVanChuyenRepository.findByIdAndNguoiDungId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
        clearDefaultAddress(userId);
        address.setLaMacDinh(true);
        address.setTrangThai(true);
        diaChiVanChuyenRepository.save(address);
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

    public List<Tinh> getAllTinh() {
        return tinhRepository.findByTrangThaiTrueOrderByTenTinhAsc();
    }

    public List<Huyen> getHuyenByTinh(Integer tinhId) {
        return huyenRepository.findByTinhIdAndTrangThaiTrueOrderByTenHuyenAsc(tinhId);
    }

    public List<Xa> getXaByHuyen(Integer huyenId) {
        return xaRepository.findByHuyenIdAndTrangThaiTrueOrderByTenXaAsc(huyenId);
    }

    private void clearDefaultAddress(Integer userId) {
        List<DiaChiVanChuyen> addresses = diaChiVanChuyenRepository.findByNguoiDungId(userId);
        for (DiaChiVanChuyen item : addresses) {
            if (Boolean.TRUE.equals(item.getLaMacDinh())) {
                item.setLaMacDinh(false);
            }
        }
        diaChiVanChuyenRepository.saveAll(addresses);
    }

    private void syncShippingAddressToAddressBook(NguoiDung user, OrderRequest request) {
        if (request.getDiaChiChiTiet() == null || request.getDiaChiChiTiet().isBlank()) {
            return;
        }

        List<DiaChiVanChuyen> addresses = diaChiVanChuyenRepository.findByNguoiDungId(user.getId());
        DiaChiVanChuyen matchedAddress = addresses.stream()
                .filter(item -> sameText(item.getTenNguoiNhan(), request.getTenNguoiNhan()))
                .filter(item -> sameText(item.getSoDienThoai(), request.getSoDienThoai()))
                .filter(item -> sameText(item.getDiaChiChiTiet(), request.getDiaChiChiTiet()))
                .filter(item -> {
                    Xa xa = item.getXa();
                    return xa != null && sameText(xa.getTenXa(), request.getXa());
                })
                .findFirst()
                .orElse(null);

        DiaChiVanChuyen address = matchedAddress != null ? matchedAddress : new DiaChiVanChuyen();
        address.setNguoiDung(user);
        address.setTenNguoiNhan(request.getTenNguoiNhan());
        address.setSoDienThoai(request.getSoDienThoai());
        address.setDiaChiChiTiet(request.getDiaChiChiTiet());
        address.setLoaiDiaChi(address.getLoaiDiaChi() != null ? address.getLoaiDiaChi() : "Nhà riêng");
        address.setTrangThai(true);
        address.setXa(resolveXaByOrderRequest(request));

        boolean hasActiveAddress = addresses.stream().anyMatch(item -> Boolean.TRUE.equals(item.getTrangThai()));
        if (matchedAddress == null) {
            address.setLaMacDinh(!hasActiveAddress);
        } else if (address.getLaMacDinh() == null) {
            address.setLaMacDinh(false);
        }

        diaChiVanChuyenRepository.save(address);
    }

    private Xa resolveXaByOrderRequest(OrderRequest request) {
        Tinh tinh = tinhRepository.findByTrangThaiTrueOrderByTenTinhAsc().stream()
                .filter(item -> sameText(item.getTenTinh(), request.getTinh()))
                .findFirst()
                .orElse(null);
        if (tinh == null) return null;

        Huyen huyen = huyenRepository.findByTinhIdAndTrangThaiTrueOrderByTenHuyenAsc(tinh.getId()).stream()
                .filter(item -> sameText(item.getTenHuyen(), request.getHuyen()))
                .findFirst()
                .orElse(null);
        if (huyen == null) return null;

        return xaRepository.findByHuyenIdAndTrangThaiTrueOrderByTenXaAsc(huyen.getId()).stream()
                .filter(item -> sameText(item.getTenXa(), request.getXa()))
                .findFirst()
                .orElse(null);
    }

    private boolean sameText(String left, String right) {
        return normalizeText(left).equals(normalizeText(right));
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private AddressResponse toAddressResponse(DiaChiVanChuyen address) {
        Xa xa = address.getXa();
        Huyen huyen = xa != null ? xa.getHuyen() : null;
        Tinh tinh = huyen != null ? huyen.getTinh() : null;
        return AddressResponse.builder()
                .id(address.getId())
                .tenNguoiNhan(address.getTenNguoiNhan())
                .soDienThoai(address.getSoDienThoai())
                .diaChiChiTiet(address.getDiaChiChiTiet())
                .loaiDiaChi(address.getLoaiDiaChi())
                .laMacDinh(address.getLaMacDinh())
                .trangThai(address.getTrangThai())
                .xaId(xa != null ? xa.getId() : null)
                .huyenId(huyen != null ? huyen.getId() : null)
                .tinhId(tinh != null ? tinh.getId() : null)
                .xa(xa != null ? xa.getTenXa() : null)
                .huyen(huyen != null ? huyen.getTenHuyen() : null)
                .tinh(tinh != null ? tinh.getTenTinh() : null)
                .build();
    }
}
