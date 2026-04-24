package com.example.sport_be.service;

import com.example.sport_be.dto.CheckoutResponse;
import com.example.sport_be.dto.AddressRequest;
import com.example.sport_be.dto.AddressResponse;
import com.example.sport_be.dto.OrderRequest;
import com.example.sport_be.dto.OrderResponse;
import com.example.sport_be.dto.ProductResponse;
import com.example.sport_be.dto.DoiTraRequest;
import com.example.sport_be.dto.DoiTraChiTietRequest;
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
import java.util.LinkedHashMap;
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
    private final DoiTraRepository doiTraRepository;
    private final DoiTraChiTietRepository doiTraChiTietRepository;
    private final DoiTraService doiTraService;
    
    private BigDecimal getPromotionPrice(SanPham sp) {
        BigDecimal giaGoc = sp.getGiaGoc();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // TÃ¬m táº¥t cáº£ cÃ¡c biáº¿n thá»ƒ cá»§a sáº£n pháº©m nÃ y
        List<SanPhamChiTiet> variants = sanPhamChiTietRepository.findBySanPhamId(sp.getId());
        
        // TÃ¬m giÃ¡ tháº¥p nháº¥t sau giáº£m trong táº¥t cáº£ biáº¿n thá»ƒ
        BigDecimal minPrice = variants.stream()
            .map(v -> {
                BigDecimal giaBan = v.getGiaBan();
                GiamGiaSanPham activeGG = giamGiaSanPhamRepository.findAll().stream()
                    .filter(gg -> gg.getSanPhamChiTiet() != null && gg.getSanPhamChiTiet().getId().equals(v.getId()))
                    .filter(gg -> gg.getDotGiamGia() != null && Boolean.TRUE.equals(gg.getDotGiamGia().getTrangThai()))
                    .filter(gg -> gg.getDotGiamGia().getNgayBatDau() != null && gg.getDotGiamGia().getNgayBatDau().isBefore(now))
                    .filter(gg -> gg.getDotGiamGia().getNgayKetHuc() != null && gg.getDotGiamGia().getNgayKetHuc().isAfter(now))
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
            })
            .min(BigDecimal::compareTo)
            .orElse(giaGoc);

        return minPrice;
    }

    private BigDecimal getPromotionPriceForVariant(SanPhamChiTiet spct) {
        BigDecimal giaBan = spct.getGiaBan();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        GiamGiaSanPham activeGG = giamGiaSanPhamRepository.findAll().stream()
            .filter(gg -> gg.getSanPhamChiTiet() != null && gg.getSanPhamChiTiet().getId().equals(spct.getId()))
            .filter(gg -> gg.getDotGiamGia() != null && Boolean.TRUE.equals(gg.getDotGiamGia().getTrangThai()))
            .filter(gg -> gg.getDotGiamGia().getNgayBatDau() != null && gg.getDotGiamGia().getNgayBatDau().isBefore(now))
            .filter(gg -> gg.getDotGiamGia().getNgayKetHuc() != null && gg.getDotGiamGia().getNgayKetHuc().isAfter(now))
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
                .chatLieu(p.getChatLieu())
                .giaGoc(p.getGiaGoc())
                .giaSauGiam(getPromotionPrice(p))
                .giaBanMin(minPrice)
                .trangThai(p.getTrangThai())
                .hinhAnhs(p.getHinhAnhs())
                .build();
    }

    public List<SanPhamChiTiet> getProductVariants(Integer productId) {
        List<SanPhamChiTiet> variants = sanPhamChiTietRepository.findBySanPhamId(productId);
        for (SanPhamChiTiet v : variants) {
            v.setGiaSauGiam(getPromotionPriceForVariant(v));
        }
        return variants;
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
                .orElseThrow(() -> new RuntimeException("NgÆ°á»i dÃ¹ng khÃ´ng tá»“n táº¡i"));
        SanPham product = sanPhamRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sáº£n pháº©m khÃ´ng tá»“n táº¡i"));

        List<SanPhamYeuThich> existingList = sanPhamYeuThichRepository.findByNguoiDungIdAndSanPhamId(userId, productId);
        if (!existingList.isEmpty()) {
            sanPhamYeuThichRepository.delete(existingList.get(0));
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
        List<GioHang> existingCarts = gioHangRepository.findByNguoiDungIdAndTrangThaiAndLoaiGioHang(userId, "DANG_SU_DUNG", "ONLINE");
        if (!existingCarts.isEmpty()) {
            return existingCarts.get(0);
        }
        
        NguoiDung user = nguoiDungRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        GioHang newCart = new GioHang();
        newCart.setNguoiDung(user);
        newCart.setLoaiGioHang("ONLINE");
        newCart.setTrangThai("DANG_SU_DUNG");
        return gioHangRepository.save(newCart);
    }

    @Transactional
    public void addToCart(Integer userId, Integer spctId, Integer quantity) {
        if (quantity == null || quantity == 0) {
            throw new RuntimeException("Số lượng sản phẩm không hợp lệ");
        }

        GioHang cart = getOrCreateCart(userId);
        if (cart == null) {
            throw new RuntimeException("NgÆ°á»i dÃ¹ng khÃ´ng tá»“n táº¡i hoáº·c khÃ´ng há»£p lá»‡");
        }
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(spctId)
                .orElseThrow(() -> new RuntimeException("Sáº£n pháº©m khÃ´ng tá»“n táº¡i"));
        
        // TÃ­nh giÃ¡ sau giáº£m náº¿u cÃ³ khuyáº¿n mÃ£i
        BigDecimal donGia = getPromotionPriceForVariant(spct);
        int stock = spct.getSoLuong() != null ? spct.getSoLuong() : 0;
        Optional<GioHangChiTiet> existingItem = gioHangChiTietRepository
                .findByGioHangIdAndSanPhamChiTietId(cart.getId(), spctId);
        int currentQty = existingItem.map(GioHangChiTiet::getSoLuong).orElse(0);

        if (quantity < 0 && currentQty + quantity < 1) {
            throw new RuntimeException("Số lượng sản phẩm không hợp lệ");
        }

        if (quantity > 0 && currentQty + quantity > stock) {
            throw new RuntimeException("Vượt quá tồn kho");
        }
        
        // Sá»­ dá»¥ng native insert Ä‘á»ƒ kÃ­ch hoáº¡t trigger trg_cart_insert xá»­ lÃ½ UPSERT
        String ma = "GHCT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        gioHangChiTietRepository.nativeAddToCart(cart.getId(), spctId, quantity, donGia, ma);
    }

    @Transactional
    public void removeFromCart(Integer userId, Integer ghctId) {
        GioHang cart = getOrCreateCart(userId);
        if (cart == null) {
            throw new RuntimeException("NgÆ°á»i dÃ¹ng khÃ´ng tá»“n táº¡i hoáº·c khÃ´ng há»£p lá»‡");
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
        
        // Cáº­p nháº­t giÃ¡ khuyáº¿n máº¡i cho tá»«ng item dá»±a trÃªn SPCT
        for (GioHangChiTiet item : items) {
            if (item.getSanPhamChiTiet() != null) {
                // Láº¥y giÃ¡ khuyáº¿n mÃ£i thá»±c táº¿ cho biáº¿n thá»ƒ SPCT nÃ y
                BigDecimal giaSauGiam = getPromotionPriceForVariant(item.getSanPhamChiTiet());
                item.setDonGia(giaSauGiam);
                
                // Äá»“ng bá»™ cáº£ giaSauGiam trong Ä‘á»‘i tÆ°á»£ng SanPham Ä‘á»ƒ FE dá»… dÃ¹ng
                if (item.getSanPhamChiTiet().getSanPham() != null) {
                    item.getSanPhamChiTiet().getSanPham().setGiaSauGiam(getPromotionPrice(item.getSanPhamChiTiet().getSanPham()));
                }
            }
        }
        
        return items;
    }

    private List<GioHangChiTiet> loadCartItemsForCheckout(Integer userId, List<Integer> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new RuntimeException("Vui lÃ²ng chá»n Ã­t nháº¥t má»™t sáº£n pháº©m");
        }

        GioHang cart = getOrCreateCart(userId);
        if (cart == null) {
            throw new RuntimeException("KhÃ´ng tÃ¬m tháº¥y giá» hÃ ng cá»§a ngÆ°á»i dÃ¹ng");
        }

        List<GioHangChiTiet> cartItems = new ArrayList<>();
        for (Integer ghctId : cartItemIds) {
            GioHangChiTiet ghct = gioHangChiTietRepository.findById(ghctId)
                    .orElseThrow(() -> new RuntimeException("Sáº£n pháº©m trong giá» khÃ´ng tá»“n táº¡i: ID=" + ghctId));

            if (ghct.getGioHang() == null || !cart.getId().equals(ghct.getGioHang().getId())) {
                throw new RuntimeException("CÃ³ sáº£n pháº©m khÃ´ng thuá»™c giá» hÃ ng cá»§a báº¡n");
            }

            cartItems.add(ghct);
        }

        return cartItems;
    }

    private void validateCartItemStock(List<GioHangChiTiet> cartItems, boolean lockInventory) {
        if (cartItems == null || cartItems.isEmpty()) {
            return;
        }

        Map<Integer, Integer> requestedByVariant = new LinkedHashMap<>();
        for (GioHangChiTiet item : cartItems) {
            if (item.getSanPhamChiTiet() == null || item.getSanPhamChiTiet().getId() == null) {
                throw new RuntimeException("Sáº£n pháº©m trong giá» khÃ´ng há»£p lá»‡");
            }

            int requestedQty = item.getSoLuong() != null ? item.getSoLuong() : 0;
            if (requestedQty <= 0) {
                String productName = item.getSanPhamChiTiet().getSanPham() != null
                        ? item.getSanPhamChiTiet().getSanPham().getTenSanPham()
                        : "Sáº£n pháº©m";
                throw new RuntimeException(productName + " cÃ³ sá»‘ lÆ°á»£ng trong giá» khÃ´ng há»£p lá»‡");
            }

            requestedByVariant.merge(item.getSanPhamChiTiet().getId(), requestedQty, Integer::sum);
        }

        List<Integer> variantIds = new ArrayList<>(requestedByVariant.keySet());
        List<SanPhamChiTiet> variants = lockInventory
                ? sanPhamChiTietRepository.findAllByIdInForUpdate(variantIds)
                : sanPhamChiTietRepository.findAllById(variantIds);

        Map<Integer, SanPhamChiTiet> variantMap = variants.stream()
                .collect(Collectors.toMap(SanPhamChiTiet::getId, spct -> spct));

        for (Map.Entry<Integer, Integer> entry : requestedByVariant.entrySet()) {
            Integer variantId = entry.getKey();
            Integer requestedQty = entry.getValue();
            SanPhamChiTiet currentVariant = variantMap.get(variantId);

            if (currentVariant == null) {
                throw new RuntimeException("Sáº£n pháº©m khÃ´ng cÃ²n tá»“n táº¡i");
            }

            int availableQty = currentVariant.getSoLuong() != null ? currentVariant.getSoLuong() : 0;
            if (requestedQty > availableQty) {
                String productName = currentVariant.getSanPham() != null
                        ? currentVariant.getSanPham().getTenSanPham()
                        : "Sáº£n pháº©m";
                throw new RuntimeException(
                        productName + " chá»‰ cÃ²n " + availableQty + " sáº£n pháº©m trong kho, vui lÃ²ng cáº­p nháº­t láº¡i giá» hÃ ng");
            }
        }
    }

    // --- Checkout & Order ---

    private BigDecimal calculatePhiVanChuyen(String tenTinh) {
        if (tenTinh == null || tenTinh.isBlank()) {
            return BigDecimal.valueOf(30000);
        }
        return tinhRepository.findByTenTinh(tenTinh)
                .map(Tinh::getPhiShipMacDinh)
                .orElse(BigDecimal.valueOf(30000));
    }

    /**
     * Chá»©c nÄƒng Thanh toÃ¡n (Checkout)
     * TÃ­nh toÃ¡n thÃ´ng tin Ä‘Æ¡n hÃ ng trÆ°á»›c khi Ä‘áº·t
     */
    public CheckoutResponse getCheckoutInfo(OrderRequest request) {
        if (request.getCartItemIds() == null || request.getCartItemIds().isEmpty()) {
            BigDecimal defaultPhi = calculatePhiVanChuyen(request.getTinh());
            return CheckoutResponse.builder()
                    .tongTienHang(BigDecimal.ZERO)
                    .tienGiam(BigDecimal.ZERO)
                    .phiVanChuyen(defaultPhi)
                    .tongThanhToan(defaultPhi)
                    .items(new ArrayList<>())
                    .build();
        }
        
        if (request.getUserId() == null) {
            throw new RuntimeException("User ID is required");
        }

        List<GioHangChiTiet> cartItems = loadCartItemsForCheckout(request.getUserId(), request.getCartItemIds());
        validateCartItemStock(cartItems, false);

        BigDecimal tongTienHang = BigDecimal.ZERO;
        
        List<CheckoutResponse.CartItemInfo> items = new ArrayList<>();
        
        for (Integer ghctId : request.getCartItemIds()) {
            GioHangChiTiet ghct = gioHangChiTietRepository.findById(ghctId)
                    .orElseThrow(() -> new RuntimeException("Sáº£n pháº©m trong giá» khÃ´ng tá»“n táº¡i: ID=" + ghctId));
            SanPhamChiTiet spct = ghct.getSanPhamChiTiet();
            // Láº¥y giÃ¡ bÃ¡n sau khi Ã¡p dá»¥ng khuyáº¿n mÃ£i
            BigDecimal donGia = getPromotionPriceForVariant(spct);
            BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(ghct.getSoLuong()));
            tongTienHang = tongTienHang.add(thanhTien);
            
            items.add(CheckoutResponse.CartItemInfo.builder()
                    .spctId(spct.getId())
                    .tenSanPham(spct.getSanPham().getTenSanPham())
                    .kichThuoc(spct.getKichThuoc() != null ? spct.getKichThuoc().getTen() : "N/A")
                    .mauSac(spct.getMauSac() != null ? spct.getMauSac().getTen() : "N/A")
                    .chatLieu(spct.getSanPham().getChatLieu() != null ? spct.getSanPham().getChatLieu().getTen() : "N/A")
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
        
        BigDecimal phiVanChuyen = calculatePhiVanChuyen(request.getTinh());
        
        return CheckoutResponse.builder()
                .tongTienHang(tongTienHang)
                .tienGiam(tienGiam)
                .phiVanChuyen(phiVanChuyen)
                .tongThanhToan(tongTienHang.subtract(tienGiam).add(phiVanChuyen))
                .items(items)
                .build();
    }

    /**
     * Chá»©c nÄƒng Äáº·t hÃ ng (Place Order)
     * LÆ°u Ä‘Æ¡n hÃ ng vÃ  chi tiáº¿t, dá»±a vÃ o trigger trg_xu_ly_hoa_don_chi_tiet Ä‘á»ƒ xá»­ lÃ½ tá»“n kho vÃ  tá»•ng tiá»n
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request, HttpServletRequest httpRequest) {
        if (request.getUserId() == null) {
            throw new RuntimeException("User ID is required");
        }
        NguoiDung user = nguoiDungRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("NgÆ°á»i dÃ¹ng khÃ´ng tá»“n táº¡i"));
        List<GioHangChiTiet> cartItems = loadCartItemsForCheckout(request.getUserId(), request.getCartItemIds());
        validateCartItemStock(cartItems, true);
        
        PtThanhToan ptThanhToan = ptThanhToanRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("PhÆ°Æ¡ng thá»©c thanh toÃ¡n khÃ´ng tá»“n táº¡i"));

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
        hoaDon.setPhiVanChuyen(calculatePhiVanChuyen(request.getTinh()));
        hoaDon.setTrangThaiDon("CHO_XAC_NHAN");
        hoaDon.setLoaiDonHang("ONLINE");
        
        // TÃ­nh toÃ¡n giáº£m giÃ¡ tá»« voucher
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
        
        // Äáº·t giÃ¡ trá»‹ máº·c Ä‘á»‹nh, trigger sáº½ cáº­p nháº­t láº¡i sau khi chÃ¨n hoa_don_chi_tiet
        hoaDon.setTongTienHang(BigDecimal.ZERO);
        hoaDon.setTongThanhToan(BigDecimal.ZERO);
        
        HoaDon savedOrder = hoaDonRepository.saveAndFlush(hoaDon);
        syncShippingAddressToAddressBook(user, request);
        
        // LÆ°u lá»‹ch sá»­ hÃ³a Ä‘Æ¡n
        LichSuHoaDon history = new LichSuHoaDon();
        history.setHoaDon(savedOrder);
        history.setTrangThaiMoi("CHO_XAC_NHAN");
        history.setLoaiHanhDong("CREATE_BILL");
        history.setHanhDong("VNPAY".equals(ptThanhToan.getMaPtThanhToan())
                ? "ÄÆ¡n hÃ ng Ä‘Ã£ táº¡o, Ä‘ang chá» thanh toÃ¡n VNPay"
                : "ÄÆ¡n hÃ ng Ä‘Ã£ Ä‘Æ°á»£c táº¡o");
        lichSuHoaDonRepository.save(history);
        
        for (Integer ghctId : request.getCartItemIds()) {
            GioHangChiTiet ghct = gioHangChiTietRepository.findById(ghctId)
                    .orElseThrow(() -> new RuntimeException("Sáº£n pháº©m trong giá» khÃ´ng tá»“n táº¡i: ID=" + ghctId));
            SanPhamChiTiet spct = ghct.getSanPhamChiTiet();
            
            // Láº¥y giÃ¡ bÃ¡n sau khi Ã¡p dá»¥ng khuyáº¿n mÃ£i
            BigDecimal donGia = getPromotionPriceForVariant(spct);
            
            HoaDonChiTiet detail = new HoaDonChiTiet();
            detail.setMaHoaDonChiTiet("HDCT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            detail.setHoaDon(savedOrder);
            detail.setSanPhamChiTiet(spct);
            detail.setTenSanPham(spct.getSanPham() != null ? spct.getSanPham().getTenSanPham() : "N/A");
            detail.setKichThuoc(spct.getKichThuoc() != null ? spct.getKichThuoc().getTen() : "N/A");
            detail.setMauSac(spct.getMauSac() != null ? spct.getMauSac().getTen() : "N/A");
            detail.setChatLieu(spct.getSanPham() != null && spct.getSanPham().getChatLieu() != null ? spct.getSanPham().getChatLieu().getTen() : "N/A");
            detail.setDonGia(donGia);
            detail.setSoLuong(ghct.getSoLuong());
            detail.setThanhTien(donGia.multiply(BigDecimal.valueOf(ghct.getSoLuong())));

            // Trigger trg_update_tong_tien_hd sáº½ tá»± Ä‘á»™ng:
            // Cáº­p nháº­t tong_tien_hang vÃ  tong_thanh_toan cá»§a hoaDon
            hoaDonChiTietRepository.save(detail);
            
            // XÃ³a khá»i giá» hÃ ng
            gioHangChiTietRepository.delete(ghct);
        }
        
        // Refresh Ä‘á»ƒ láº¥y dá»¯ liá»‡u Ä‘Ã£ Ä‘Æ°á»£c trigger cáº­p nháº­t trong database
        entityManager.flush();
        savedOrder = hoaDonRepository.findById(savedOrder.getId())
                .orElseThrow(() -> new RuntimeException("KhÃ´ng tÃ¬m tháº¥y Ä‘Æ¡n hÃ ng sau khi táº¡o"));
        
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
                history.setTrangThaiMoi("CHO_XAC_NHAN");
                history.setLoaiHanhDong("PAYMENT");
                history.setHanhDong("Thanh toÃ¡n VNPAY thÃ nh cÃ´ng");
                lichSuHoaDonRepository.save(history);
            }
            return frontendRedirectBase + "?payment=success&orderId=" + hoaDon.getId();
        }

        if (!"DA_HUY".equals(hoaDon.getTrangThaiDon())) {
            hoaDon.setTrangThaiDon("DA_HUY");
            hoaDonRepository.save(hoaDon);

            LichSuHoaDon history = new LichSuHoaDon();
            history.setHoaDon(hoaDon);
            history.setTrangThaiMoi("DA_HUY");
            history.setLoaiHanhDong("PAYMENT_FAILED");
            history.setHanhDong("Thanh toan VNPAY that bai hoac bi huy");
            lichSuHoaDonRepository.save(history);
        }

        return frontendRedirectBase + "?payment=failed&orderId=" + hoaDon.getId();
    }

    public List<HoaDon> getUserOrders(Integer userId) {
        return hoaDonRepository.findByNguoiDungIdOrderByNgayTaoDesc(userId);
    }

    @Transactional
    public void confirmReceived(Integer orderId) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        String currentStatus = hoaDon.getTrangThaiDon() != null ? hoaDon.getTrangThaiDon().trim() : "";
        
        if (!"DANG_GIAO".equalsIgnoreCase(currentStatus)) {
            throw new RuntimeException("Chỉ có thể xác nhận khi đơn hàng đang giao. Trạng thái hiện tại: " + currentStatus);
        }

        hoaDon.setTrangThaiDon("DA_GIAO");
        hoaDon.setNgayNhanHang(java.time.LocalDateTime.now());
        hoaDonRepository.save(hoaDon);

        LichSuHoaDon history = new LichSuHoaDon();
        history.setHoaDon(hoaDon);
        history.setTrangThaiCu(currentStatus);
        history.setTrangThaiMoi("DA_GIAO");
        history.setLoaiHanhDong("UPDATE_STATUS");
        history.setHanhDong("Khách hàng đã xác nhận nhận hàng");
        lichSuHoaDonRepository.save(history);
    }

    public Object getOrderById(Integer orderId) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId).orElse(null);
        if (hoaDon == null) return null;
        
        List<HoaDonChiTiet> items = hoaDonChiTietRepository.findByHoaDonId(orderId);
        List<LichSuHoaDon> history = lichSuHoaDonRepository.findByHoaDonIdOrderByThoiGianDesc(orderId);
        List<DoiTra> returns = doiTraRepository.findByHoaDonId(orderId);
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("bill", toUserOrderBillMap(hoaDon));
        response.put("items", items.stream().map(this::toUserOrderItemMap).toList());
        response.put("history", history);
        response.put("returns", returns);
        return response;
    }

    private Map<String, Object> toUserOrderBillMap(HoaDon hoaDon) {
        DiaChiVanChuyen matchedAddress = findMatchingOrderAddress(hoaDon);
        Xa matchedXa = matchedAddress != null ? matchedAddress.getXa() : null;
        Huyen matchedHuyen = matchedXa != null ? matchedXa.getHuyen() : null;
        Tinh matchedTinh = matchedHuyen != null ? matchedHuyen.getTinh() : null;

        String tenNguoiNhan = preferReadableText(
                hoaDon.getTenNguoiNhan(),
                matchedAddress != null ? matchedAddress.getTenNguoiNhan()
                        : hoaDon.getNguoiDung() != null ? hoaDon.getNguoiDung().getHoTen() : null
        );
        String soDienThoai = isBrokenVietnamese(hoaDon.getSoDienThoai()) ? null : hoaDon.getSoDienThoai();
        if ((soDienThoai == null || soDienThoai.isBlank()) && matchedAddress != null) {
            soDienThoai = isBrokenVietnamese(matchedAddress.getSoDienThoai()) ? null : matchedAddress.getSoDienThoai();
        }

        String diaChiChiTiet = isBrokenVietnamese(hoaDon.getDiaChiChiTiet()) ? null : hoaDon.getDiaChiChiTiet();
        if ((diaChiChiTiet == null || diaChiChiTiet.isBlank()) && matchedAddress != null) {
            diaChiChiTiet = isBrokenVietnamese(matchedAddress.getDiaChiChiTiet()) ? null : matchedAddress.getDiaChiChiTiet();
        }

        String tinh = isBrokenVietnamese(hoaDon.getTinh()) ? null : hoaDon.getTinh();
        String huyen = isBrokenVietnamese(hoaDon.getHuyen()) ? null : hoaDon.getHuyen();
        String xa = isBrokenVietnamese(hoaDon.getXa()) ? null : hoaDon.getXa();
        if ((tinh == null || tinh.isBlank()) && matchedTinh != null) {
            tinh = matchedTinh.getTenTinh();
        }
        if ((huyen == null || huyen.isBlank()) && matchedHuyen != null) {
            huyen = matchedHuyen.getTenHuyen();
        }
        if ((xa == null || xa.isBlank()) && matchedXa != null) {
            xa = matchedXa.getTenXa();
        }

        Map<String, Object> bill = new java.util.LinkedHashMap<>();
        bill.put("id", hoaDon.getId());
        bill.put("maHoaDon", hoaDon.getMaHoaDon());
        bill.put("nguoiDung", hoaDon.getNguoiDung());
        bill.put("tenNguoiNhan", tenNguoiNhan);
        bill.put("soDienThoai", soDienThoai);
        bill.put("email", hoaDon.getNguoiDung() != null ? hoaDon.getNguoiDung().getEmail() : null);
        bill.put("tinh", tinh);
        bill.put("huyen", huyen);
        bill.put("xa", xa);
        bill.put("diaChiChiTiet", diaChiChiTiet);
        bill.put("tongTienHang", hoaDon.getTongTienHang());
        bill.put("tienGiam", hoaDon.getTienGiam());
        bill.put("phiVanChuyen", hoaDon.getPhiVanChuyen());
        bill.put("tongThanhToan", hoaDon.getTongThanhToan());
        bill.put("trangThaiDon", hoaDon.getTrangThaiDon());
        bill.put("loaiDonHang", hoaDon.getLoaiDonHang());
        bill.put("ngayTao", hoaDon.getNgayTao());
        bill.put("ptThanhToan", hoaDon.getPtThanhToan());
        bill.put("maGiamGia", hoaDon.getMaGiamGia());
        return bill;
    }

    private Map<String, Object> toUserOrderItemMap(HoaDonChiTiet item) {
        SanPhamChiTiet spct = item.getSanPhamChiTiet();
        SanPham sanPham = spct != null ? spct.getSanPham() : null;

        Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("id", item.getId());
        result.put("tenSanPham", preferReadableText(item.getTenSanPham(), sanPham != null ? sanPham.getTenSanPham() : null));
        result.put("kichThuoc", sanitizeUserText(item.getKichThuoc()));
        result.put("mauSac", preferReadableText(item.getMauSac(), spct != null && spct.getMauSac() != null ? spct.getMauSac().getTen() : null));
        result.put("chatLieu", sanitizeUserText(item.getChatLieu()));
        result.put("donGia", item.getDonGia());
        result.put("soLuong", item.getSoLuong());
        result.put("thanhTien", item.getThanhTien());

        Map<String, Object> sanPhamChiTiet = new java.util.LinkedHashMap<>();
        sanPhamChiTiet.put("id", spct != null ? spct.getId() : null);

        if (sanPham != null) {
            Map<String, Object> sanPhamMap = new java.util.LinkedHashMap<>();
            sanPhamMap.put("id", sanPham.getId());
            sanPhamMap.put("tenSanPham", sanPham.getTenSanPham());
            sanPhamMap.put("hinhAnhs", sanPham.getHinhAnhs());
            sanPhamChiTiet.put("sanPham", sanPhamMap);
        } else {
            sanPhamChiTiet.put("sanPham", null);
        }

        result.put("sanPhamChiTiet", sanPhamChiTiet);
        return result;
    }

    private DiaChiVanChuyen findMatchingOrderAddress(HoaDon hoaDon) {
        if (hoaDon.getNguoiDung() == null || hoaDon.getNguoiDung().getId() == null) {
            return null;
        }

        List<DiaChiVanChuyen> addresses = diaChiVanChuyenRepository
                .findByNguoiDungIdAndTrangThaiTrueOrderByLaMacDinhDescIdDesc(hoaDon.getNguoiDung().getId());

        return addresses.stream()
                .filter(item -> sameText(item.getTenNguoiNhan(), hoaDon.getTenNguoiNhan()))
                .filter(item -> sameText(item.getSoDienThoai(), hoaDon.getSoDienThoai()))
                .filter(item -> sameText(item.getDiaChiChiTiet(), hoaDon.getDiaChiChiTiet()))
                .findFirst()
                .orElseGet(() -> addresses.stream()
                        .filter(item -> sameText(item.getDiaChiChiTiet(), hoaDon.getDiaChiChiTiet()))
                        .findFirst()
                        .orElseGet(() -> addresses.stream().findFirst().orElse(null)));
    }

    private String preferReadableText(String snapshotValue, String fallbackValue) {
        if (isBrokenVietnamese(snapshotValue) && fallbackValue != null && !fallbackValue.isBlank()) {
            return fallbackValue;
        }
        return sanitizeUserText(snapshotValue);
    }

    private String sanitizeAddressText(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return isBrokenVietnamese(value) ? "Dá»¯ liá»‡u Ä‘á»‹a chá»‰ cÅ© bá»‹ lá»—i mÃ£ hÃ³a" : value;
    }

    private String sanitizeUserText(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        // Fix common known broken snapshots
        if (value.trim().equalsIgnoreCase("Khach l?") || value.trim().equalsIgnoreCase("KhÃ¡ch l?")) {
            return "KhÃ¡ch láº»";
        }
        return isBrokenVietnamese(value) ? "Dá»¯ liá»‡u cÅ© bá»‹ lá»—i mÃ£ hÃ³a" : value;
    }

    private boolean isBrokenVietnamese(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return value.contains("?")
                || value.contains("Ãƒ")
                || value.contains("Ã†")
                || value.contains("Ã")
                || value.contains("ï¿½");
    }

    @Transactional
    public void cancelOrder(Integer orderId) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("ÄÆ¡n hÃ ng khÃ´ng tá»“n táº¡i"));
        
        // Cho phÃ©p há»§y á»Ÿ tráº¡ng thÃ¡i Chá» xÃ¡c nháº­n hoáº·c ÄÃ£ xÃ¡c nháº­n
        if (!"CHO_XAC_NHAN".equals(hoaDon.getTrangThaiDon()) && !"DA_XAC_NHAN".equals(hoaDon.getTrangThaiDon())) {
            throw new RuntimeException("Chá»‰ cÃ³ thá»ƒ há»§y Ä‘Æ¡n hÃ ng á»Ÿ tráº¡ng thÃ¡i Chá» xÃ¡c nháº­n hoáº·c ÄÃ£ xÃ¡c nháº­n");
        }
        
        String oldStatus = hoaDon.getTrangThaiDon();
        hoaDon.setTrangThaiDon("DA_HUY");
        hoaDonRepository.save(hoaDon);
        
        // Log history
        LichSuHoaDon history = new LichSuHoaDon();
        history.setHoaDon(hoaDon);
        history.setTrangThaiCu(oldStatus);
        history.setTrangThaiMoi("DA_HUY");
        history.setLoaiHanhDong("UPDATE_STATUS");
        history.setHanhDong("KhÃ¡ch hÃ ng há»§y Ä‘Æ¡n hÃ ng");
        lichSuHoaDonRepository.save(history);
    }
     // --- Đổi Trả (User yêu cầu) ---

    /**
     * User tạo yêu cầu đổi trả sản phẩm.
     * Check hóa đơn DA_GIAO và <= 7 ngày. Phí ship không hoàn.
     * Ảnh bắt buộc.
     */
    @Transactional
    public DoiTra createDoiTraRequest(DoiTraRequest request, org.springframework.web.multipart.MultipartFile[] files, String baseUrl) {
        return doiTraService.taoYeuCauDoiTra(request, files, baseUrl);
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
