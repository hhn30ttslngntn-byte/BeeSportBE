package com.example.sport_be.service;

import com.example.sport_be.entity.*;
import com.example.sport_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final SanPhamRepository sanPhamRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final DanhMucRepository danhMucRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final HoaDonRepository hoaDonRepository;
    private final MaGiamGiaRepository maGiamGiaRepository;
    private final DotGiamGiaRepository dotGiamGiaRepository;
    private final GiamGiaSanPhamRepository giamGiaSanPhamRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;
    private final ChatLieuRepository chatLieuRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final PtThanhToanRepository ptThanhToanRepository;
    private final LichSuThanhToanRepository lichSuThanhToanRepository;
    private final GioHangRepository gioHangRepository;
    private final GioHangChiTietRepository gioHangChiTietRepository;
    private final HinhAnhSanPhamRepository hinhAnhSanPhamRepository;
    private final DiaChiVanChuyenRepository diaChiVanChuyenRepository;

    // --- Product ---
    public List<SanPham> getAllProducts() {
        List<SanPham> products = sanPhamRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        
        for (SanPham sp : products) {
            List<SanPhamChiTiet> details = sanPhamChiTietRepository.findBySanPhamId(sp.getId());
            int tongSoLuong = details.stream()
                    .map(SanPhamChiTiet::getSoLuong)
                    .filter(java.util.Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .sum();
            sp.setTongSoLuong(tongSoLuong);
            // Tìm đợt giảm giá đang hoạt động cho sản phẩm này
            GiamGiaSanPham activeGG = giamGiaSanPhamRepository.findAll().stream()
                .filter(gg -> gg.getSanPham().getId().equals(sp.getId()))
                .filter(gg -> gg.getDotGiamGia().getTrangThai())
                .filter(gg -> gg.getDotGiamGia().getNgayBatDau().isBefore(now) && gg.getDotGiamGia().getNgayKetHuc().isAfter(now))
                .findFirst().orElse(null);
            
            if (activeGG != null) {
                DotGiamGia dgg = activeGG.getDotGiamGia();
                sp.setTenKhuyenMai(dgg.getTenDot());
                
                if ("PERCENT".equals(dgg.getKieuGiamGia())) {
                    BigDecimal giam = sp.getGiaGoc().multiply(dgg.getGiaTriGiam().divide(new BigDecimal(100)));
                    sp.setGiaSauGiam(sp.getGiaGoc().subtract(giam));
                } else {
                    sp.setGiaSauGiam(sp.getGiaGoc().subtract(dgg.getGiaTriGiam()));
                }
            } else {
                sp.setGiaSauGiam(sp.getGiaGoc());
            }
        }
        return products;
    }

    public SanPham getProductById(Integer id) {
        return sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Object getProductDetail(Integer id) {
        SanPham sp = getProductById(id);
        
        // Tính giá sau giảm cho sản phẩm cha
        LocalDateTime now = LocalDateTime.now();
        GiamGiaSanPham activeGG = giamGiaSanPhamRepository.findAll().stream()
            .filter(gg -> gg.getSanPham().getId().equals(sp.getId()))
            .filter(gg -> gg.getDotGiamGia().getTrangThai())
            .filter(gg -> gg.getDotGiamGia().getNgayBatDau().isBefore(now) && gg.getDotGiamGia().getNgayKetHuc().isAfter(now))
            .findFirst().orElse(null);
        
        if (activeGG != null) {
            DotGiamGia dgg = activeGG.getDotGiamGia();
            sp.setTenKhuyenMai(dgg.getTenDot());
            if ("PERCENT".equals(dgg.getKieuGiamGia())) {
                BigDecimal giam = sp.getGiaGoc().multiply(dgg.getGiaTriGiam().divide(new BigDecimal(100)));
                sp.setGiaSauGiam(sp.getGiaGoc().subtract(giam));
            } else {
                sp.setGiaSauGiam(sp.getGiaGoc().subtract(dgg.getGiaTriGiam()));
            }
        } else {
            sp.setGiaSauGiam(sp.getGiaGoc());
        }

        List<SanPhamChiTiet> details = sanPhamChiTietRepository.findBySanPhamId(id);
        
        return new Object() {
            public SanPham getProduct() { return sp; }
            public List<SanPhamChiTiet> getDetails() { return details; }
        };
    }

    @Transactional
    public SanPham saveProductWithVariants(SanPham sanPham, List<SanPhamChiTiet> variants, List<String> imageUrls) {
        // 1. Kiểm tra mã sản phẩm (chỉ khi thêm mới)
        if (sanPham.getId() == null) {
            if (sanPhamRepository.findByMa(sanPham.getMa()).isPresent()) {
                throw new RuntimeException("Mã sản phẩm '" + sanPham.getMa() + "' đã tồn tại trong hệ thống!");
            }
        }

        // 2. Gán các thuộc tính cần thiết
        if (sanPham.getDanhMuc() != null && sanPham.getDanhMuc().getId() != null) {
            sanPham.setDanhMuc(danhMucRepository.findById(sanPham.getDanhMuc().getId()).orElse(null));
        }
        if (sanPham.getThuongHieu() != null && sanPham.getThuongHieu().getId() != null) {
            sanPham.setThuongHieu(thuongHieuRepository.findById(sanPham.getThuongHieu().getId()).orElse(null));
        }

        // 3. Lưu Sản phẩm (Cha)
        SanPham savedProduct = sanPhamRepository.save(sanPham);

        // 4. Lưu Sản phẩm chi tiết (Variants)
        if (variants != null) {
            for (SanPhamChiTiet v : variants) {
                // Đảm bảo không bị NULL khóa ngoại id_san_pham
                v.setSanPham(savedProduct);
                
                // Theo SQL của bạn, id_thuong_hieu trong bảng chi tiết là NOT NULL
                // Chúng ta sẽ lấy thương hiệu của sản phẩm cha gán cho biến thể nếu biến thể chưa có
                if (v.getThuongHieu() == null || v.getThuongHieu().getId() == null) {
                    v.setThuongHieu(savedProduct.getThuongHieu());
                }
                
                // Tự sinh mã SPCT nếu chưa có
                if (v.getMa() == null || v.getMa().isEmpty()) {
                    v.setMa("SPCT-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                }

                // Gán thuộc tính con (MauSac, KichThuoc, ChatLieu)
                if (v.getMauSac() != null && v.getMauSac().getId() != null) {
                    v.setMauSac(mauSacRepository.findById(v.getMauSac().getId()).orElse(null));
                }
                if (v.getKichThuoc() != null && v.getKichThuoc().getId() != null) {
                    v.setKichThuoc(kichThuocRepository.findById(v.getKichThuoc().getId()).orElse(null));
                }
                if (v.getChatLieu() != null && v.getChatLieu().getId() != null) {
                    v.setChatLieu(chatLieuRepository.findById(v.getChatLieu().getId()).orElse(null));
                }
            }
            sanPhamChiTietRepository.saveAll(variants);
        }

        syncProductImages(savedProduct, imageUrls);

        return savedProduct;
    }

    public List<String> uploadProductImages(List<MultipartFile> files, String baseUrl) {
        try {
            Path uploadDir = Paths.get("uploads", "products").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            List<String> uploadedUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;

                String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image";
                String extension = "";
                int lastDot = originalName.lastIndexOf('.');
                if (lastDot >= 0) {
                    extension = originalName.substring(lastDot);
                }

                String fileName = UUID.randomUUID() + extension;
                Path target = uploadDir.resolve(fileName);
                file.transferTo(target.toFile());
                uploadedUrls.add(baseUrl + "/uploads/products/" + fileName);
            }
            return uploadedUrls;
        } catch (IOException e) {
            throw new RuntimeException("Khong the tai anh len", e);
        }
    }

    private void syncProductImages(SanPham product, List<String> imageUrls) {
        List<HinhAnhSanPham> currentImages = hinhAnhSanPhamRepository.findBySanPhamId(product.getId());
        if (!currentImages.isEmpty()) {
            hinhAnhSanPhamRepository.deleteAll(currentImages);
        }

        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        List<HinhAnhSanPham> newImages = imageUrls.stream()
                .filter(url -> url != null && !url.isBlank())
                .map(url -> {
                    HinhAnhSanPham image = new HinhAnhSanPham();
                    image.setSanPham(product);
                    image.setUrl(url);
                    image.setTrangThai(true);
                    return image;
                })
                .toList();

        hinhAnhSanPhamRepository.saveAll(newImages);
    }

    public void deleteProduct(Integer id) {
        sanPhamRepository.deleteById(id);
    }

    public void toggleProductStatus(Integer id) {
        SanPham sp = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        sp.setTrangThai(!sp.getTrangThai());
        sanPhamRepository.save(sp);
    }

    // --- Product Details ---
    public List<SanPhamChiTiet> getAllProductDetails() {
        return sanPhamChiTietRepository.findAll();
    }

    @Transactional
    public List<SanPhamChiTiet> saveProductDetails(List<SanPhamChiTiet> details) {
        return sanPhamChiTietRepository.saveAll(details);
    }

    // --- Category ---
    public List<DanhMuc> getAllCategories() {
        return danhMucRepository.findAll();
    }

    public DanhMuc saveCategory(DanhMuc danhMuc) {
        return danhMucRepository.save(danhMuc);
    }

    // --- Brand ---
    public List<ThuongHieu> getAllBrands() {
        return thuongHieuRepository.findByTrangThaiTrue();
    }

    public ThuongHieu saveBrand(ThuongHieu brand) {
        if (brand.getMaThuongHieu() == null || brand.getMaThuongHieu().isBlank()) {
            brand.setMaThuongHieu(generateAttributeCode(brand.getTenThuongHieu()));
        }
        return thuongHieuRepository.save(brand);
    }

    // --- User (Staff & Customer) ---
    public List<NguoiDung> getAllUsers() {
        return nguoiDungRepository.findAll();
    }

    public List<NguoiDung> getUsersByRole(Integer roleId) {
        return nguoiDungRepository.findByVaiTroId(roleId);
    }

    public NguoiDung saveUser(NguoiDung user) {
        if (user.getMaNguoiDung() == null || user.getMaNguoiDung().isEmpty()) {
            user.setMaNguoiDung("USER" + System.currentTimeMillis());
        }
        return nguoiDungRepository.save(user);
    }

    // --- Bill ---
    public List<Map<String, Object>> getAllBills() {
        return hoaDonRepository.findAll().stream()
                .sorted((left, right) -> {
                    LocalDateTime leftTime = left.getNgayTao();
                    LocalDateTime rightTime = right.getNgayTao();
                    if (leftTime == null && rightTime == null) return 0;
                    if (leftTime == null) return 1;
                    if (rightTime == null) return -1;
                    return rightTime.compareTo(leftTime);
                })
                .map(this::toBillSummaryMap)
                .toList();
    }

    public Map<String, Object> getRevenueSummary() {
        List<HoaDon> deliveredBills = hoaDonRepository.findAll().stream()
                .filter(bill -> "DA_GIAO".equals(bill.getTrangThaiDon()))
                .toList();

        LocalDate today = LocalDate.now();
        List<HoaDon> todayBills = deliveredBills.stream()
                .filter(bill -> bill.getNgayTao() != null && today.equals(bill.getNgayTao().toLocalDate()))
                .toList();

        BigDecimal todayRevenue = todayBills.stream()
                .map(HoaDon::getTongThanhToan)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int todayOrders = todayBills.size();
        int todayProductsSold = todayBills.stream()
                .map(HoaDon::getId)
                .filter(java.util.Objects::nonNull)
                .mapToInt(id -> hoaDonChiTietRepository.findByHoaDonId(id).stream()
                        .map(HoaDonChiTiet::getSoLuong)
                        .filter(java.util.Objects::nonNull)
                        .mapToInt(Integer::intValue)
                        .sum())
                .sum();

        Map<LocalDate, List<HoaDon>> groupedByDate = new java.util.TreeMap<>(java.util.Comparator.reverseOrder());
        for (HoaDon bill : deliveredBills) {
            if (bill.getNgayTao() == null) continue;
            groupedByDate.computeIfAbsent(bill.getNgayTao().toLocalDate(), key -> new ArrayList<>()).add(bill);
        }

        List<Map<String, Object>> dailyRevenue = groupedByDate.entrySet().stream()
                .map(entry -> {
                    BigDecimal revenue = entry.getValue().stream()
                            .map(HoaDon::getTongThanhToan)
                            .filter(java.util.Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    int productsSold = entry.getValue().stream()
                            .map(HoaDon::getId)
                            .filter(java.util.Objects::nonNull)
                            .mapToInt(id -> hoaDonChiTietRepository.findByHoaDonId(id).stream()
                                    .map(HoaDonChiTiet::getSoLuong)
                                    .filter(java.util.Objects::nonNull)
                                    .mapToInt(Integer::intValue)
                                    .sum())
                            .sum();

                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("date", entry.getKey());
                    item.put("orders", entry.getValue().size());
                    item.put("productsSold", productsSold);
                    item.put("revenue", revenue);
                    return item;
                })
                .toList();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("todayRevenue", todayRevenue);
        stats.put("todayOrders", todayOrders);
        stats.put("todayProductsSold", todayProductsSold);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("stats", stats);
        response.put("dailyRevenue", dailyRevenue);
        return response;
    }

    public HoaDon updateBillStatus(Integer id, String status) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
        hoaDon.setTrangThaiDon(status);
        HoaDon updated = hoaDonRepository.save(hoaDon);

        // Log history
        LichSuHoaDon history = new LichSuHoaDon();
        history.setHoaDon(updated);
        history.setTrangThai(status);
        lichSuHoaDonRepository.save(history);

        return updated;
    }

    public Object getBillDetail(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
        List<HoaDonChiTiet> items = hoaDonChiTietRepository.findByHoaDonId(id);
        List<LichSuHoaDon> history = lichSuHoaDonRepository.findByHoaDonIdOrderByNgayCapNhatDesc(id);

        Map<String, Object> response = new HashMap<>();
        response.put("bill", toBillDetailMap(hoaDon));
        response.put("items", items.stream().map(this::toBillItemMap).toList());
        response.put("history", history);
        return response;
    }

    private Map<String, Object> toBillSummaryMap(HoaDon hoaDon) {
        Map<String, Object> bill = new LinkedHashMap<>();
        bill.put("id", hoaDon.getId());
        bill.put("maHoaDon", hoaDon.getMaHoaDon());
        bill.put("tenNguoiNhan", sanitizeDisplayText(hoaDon.getTenNguoiNhan()));
        bill.put("tenKhachHang", sanitizeDisplayText(
                hoaDon.getNguoiDung() != null ? hoaDon.getNguoiDung().getHoTen() : hoaDon.getTenNguoiNhan()
        ));
        bill.put("soDienThoai", sanitizeDisplayText(hoaDon.getSoDienThoai()));
        bill.put("tongTienHang", hoaDon.getTongTienHang());
        bill.put("tienGiam", hoaDon.getTienGiam());
        bill.put("phiVanChuyen", hoaDon.getPhiVanChuyen());
        bill.put("tongThanhToan", hoaDon.getTongThanhToan());
        bill.put("trangThaiDon", hoaDon.getTrangThaiDon());
        bill.put("loaiDonHang", hoaDon.getLoaiDonHang());
        bill.put("ngayTao", hoaDon.getNgayTao());
        return bill;
    }

    private Map<String, Object> toBillDetailMap(HoaDon hoaDon) {
        DiaChiVanChuyen matchedAddress = findMatchingShippingAddress(hoaDon);
        Xa matchedXa = matchedAddress != null ? matchedAddress.getXa() : null;
        Huyen matchedHuyen = matchedXa != null ? matchedXa.getHuyen() : null;
        Tinh matchedTinh = matchedHuyen != null ? matchedHuyen.getTinh() : null;

        String xa = sanitizeRegionText(hoaDon.getXa());
        String huyen = sanitizeRegionText(hoaDon.getHuyen());
        String tinh = sanitizeRegionText(hoaDon.getTinh());

        if (xa == null && matchedXa != null) xa = matchedXa.getTenXa();
        if (huyen == null && matchedHuyen != null) huyen = matchedHuyen.getTenHuyen();
        if (tinh == null && matchedTinh != null) tinh = matchedTinh.getTenTinh();

        Map<String, Object> bill = new LinkedHashMap<>();
        bill.put("id", hoaDon.getId());
        bill.put("maHoaDon", hoaDon.getMaHoaDon());
        bill.put("tenNguoiNhan", sanitizeDisplayText(hoaDon.getTenNguoiNhan()));
        bill.put("soDienThoai", sanitizeDisplayText(hoaDon.getSoDienThoai()));
        bill.put("ghiChu", sanitizeDisplayText(hoaDon.getGhiChu()));
        bill.put("diaChiChiTiet", sanitizeDisplayText(hoaDon.getDiaChiChiTiet()));
        bill.put("xa", xa);
        bill.put("huyen", huyen);
        bill.put("tinh", tinh);
        bill.put("diaChiLoiMaHoa",
                isBrokenVietnamese(hoaDon.getXa())
                        || isBrokenVietnamese(hoaDon.getHuyen())
                        || isBrokenVietnamese(hoaDon.getTinh()));
        bill.put("tongTienHang", hoaDon.getTongTienHang());
        bill.put("tienGiam", hoaDon.getTienGiam());
        bill.put("phiVanChuyen", hoaDon.getPhiVanChuyen());
        bill.put("tongThanhToan", hoaDon.getTongThanhToan());
        bill.put("trangThaiDon", hoaDon.getTrangThaiDon());
        bill.put("loaiDonHang", hoaDon.getLoaiDonHang());
        bill.put("ngayTao", hoaDon.getNgayTao());
        return bill;
    }

    private DiaChiVanChuyen findMatchingShippingAddress(HoaDon hoaDon) {
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
                        .orElse(null));
    }

    private Map<String, Object> toBillItemMap(HoaDonChiTiet item) {
        SanPhamChiTiet spct = item.getSanPhamChiTiet();
        SanPham sanPham = spct != null ? spct.getSanPham() : null;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", item.getId());
        result.put("tenSanPham", preferCurrentValue(item.getTenSanPham(), sanPham != null ? sanPham.getTenSanPham() : null));
        result.put("mauSac", preferCurrentValue(item.getMauSac(), spct != null && spct.getMauSac() != null ? spct.getMauSac().getTen() : null));
        result.put("kichThuoc", preferCurrentValue(item.getKichThuoc(), spct != null && spct.getKichThuoc() != null ? spct.getKichThuoc().getTen() : null));
        result.put("chatLieu", preferCurrentValue(item.getChatLieu(), spct != null && spct.getChatLieu() != null ? spct.getChatLieu().getTen() : null));
        result.put("donGia", item.getDonGia());
        result.put("soLuong", item.getSoLuong());
        result.put("thanhTien", item.getThanhTien());
        return result;
    }

    private String preferCurrentValue(String snapshotValue, String currentValue) {
        if (isBrokenVietnamese(snapshotValue) && currentValue != null && !currentValue.isBlank()) {
            return currentValue;
        }
        return sanitizeDisplayText(snapshotValue);
    }

    private String sanitizeRegionText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return isBrokenVietnamese(value) ? null : value;
    }

    private String sanitizeDisplayText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return isBrokenVietnamese(value) ? "Dữ liệu cũ bị lỗi mã hóa" : value;
    }

    private boolean isBrokenVietnamese(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return value.contains("?")
                || value.contains("Ã")
                || value.contains("Æ")
                || value.contains("Ð")
                || value.contains("�");
    }

    private boolean sameText(String left, String right) {
        return normalizeText(left).equals(normalizeText(right));
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    // --- Voucher ---
    public List<MaGiamGia> getAllVouchers() {
        return maGiamGiaRepository.findAll();
    }

    public MaGiamGia saveVoucher(MaGiamGia voucher) {
        return maGiamGiaRepository.save(voucher);
    }

    public MaGiamGia getVoucherById(Integer id) {
        return maGiamGiaRepository.findById(id).orElseThrow(() -> new RuntimeException("Voucher not found"));
    }

    public void deleteVoucher(Integer id) {
        maGiamGiaRepository.deleteById(id);
    }

    // --- Promotion (DotGiamGia) ---
    public List<DotGiamGia> getAllPromotions() {
        return dotGiamGiaRepository.findAll();
    }

    public DotGiamGia getPromotionById(Integer id) {
        return dotGiamGiaRepository.findById(id).orElseThrow(() -> new RuntimeException("Promotion not found"));
    }

    @Transactional
    public DotGiamGia savePromotion(DotGiamGia promotion, List<Integer> productIds) {
        DotGiamGia saved = dotGiamGiaRepository.save(promotion);
        
        // Update product promotions
        if (productIds != null) {
            // Clear old
            giamGiaSanPhamRepository.deleteByDotGiamGiaId(saved.getId());
            
            // Add new
            for (Integer productId : productIds) {
                GiamGiaSanPham ggsp = new GiamGiaSanPham();
                ggsp.setDotGiamGia(saved);
                SanPham sp = new SanPham();
                sp.setId(productId);
                ggsp.setSanPham(sp);
                giamGiaSanPhamRepository.save(ggsp);
            }
        }
        return saved;
    }

    public void deletePromotion(Integer id) {
        giamGiaSanPhamRepository.deleteByDotGiamGiaId(id);
        dotGiamGiaRepository.deleteById(id);
    }

    public List<Integer> getProductIdsForPromotion(Integer promotionId) {
        return giamGiaSanPhamRepository.findByDotGiamGiaId(promotionId)
                .stream()
                .map(ggsp -> ggsp.getSanPham().getId())
                .collect(java.util.stream.Collectors.toList());
    }

    // --- POS Refined (Using gio_hang for waiting orders) ---
    public List<GioHang> getWaitingInvoices() { 
        return gioHangRepository.findByLoaiGioHangAndTrangThai("TAI_QUAY", "DANG_SU_DUNG"); 
    }

    public Object getWaitingInvoiceDetail(Integer id) {
        GioHang gh = gioHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn chờ"));
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(id);
        
        // Trả về cấu trúc tương đương bill detail để Frontend dễ xử lý
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("bill", gh);
        response.put("items", items);
        return response;
    }

    public GioHang createWaitingInvoice() { 
        GioHang gh = new GioHang(); 
        gh.setMa("POS-" + System.currentTimeMillis());
        gh.setTrangThai("DANG_SU_DUNG");
        gh.setLoaiGioHang("TAI_QUAY");
        
        // Gán cho admin/staff đang đăng nhập (ở đây lấy đại diện user 1)
        List<NguoiDung> users = nguoiDungRepository.findAll();
        if (!users.isEmpty()) {
            gh.setNguoiDung(users.get(0)); 
        }

        return gioHangRepository.save(gh); 
    }

    @Transactional
    public void addInvoiceDetail(Integer id, Integer spctId, Integer quantity) {
        // id ở đây là id_gio_hang (waiting order)
        GioHang gh = gioHangRepository.findById(id).orElseThrow();
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(spctId).orElseThrow();
        
        // Tính giá sau giảm nếu có khuyến mãi
        BigDecimal giaBan = spct.getGiaBan();
        LocalDateTime now = LocalDateTime.now();
        
        GiamGiaSanPham activeGG = giamGiaSanPhamRepository.findAll().stream()
            .filter(gg -> gg.getSanPham().getId().equals(spct.getSanPham().getId()))
            .filter(gg -> gg.getDotGiamGia().getTrangThai())
            .filter(gg -> gg.getDotGiamGia().getNgayBatDau().isBefore(now) && gg.getDotGiamGia().getNgayKetHuc().isAfter(now))
            .findFirst().orElse(null);
            
        if (activeGG != null) {
            DotGiamGia dgg = activeGG.getDotGiamGia();
            if ("PERCENT".equals(dgg.getKieuGiamGia())) {
                BigDecimal giam = giaBan.multiply(dgg.getGiaTriGiam().divide(new BigDecimal(100)));
                giaBan = giaBan.subtract(giam);
            } else {
                giaBan = giaBan.subtract(dgg.getGiaTriGiam());
            }
        }
        
        // Sử dụng logic nativeAddToCart hoặc thủ công để tránh trùng lặp trong giỏ
        String ma = "GHCT-POS-" + System.currentTimeMillis();
        gioHangChiTietRepository.nativeAddToCart(id, spctId, quantity, giaBan, ma);
    }

    @Transactional
    public void removeInvoiceDetail(Integer detailId) {
        gioHangChiTietRepository.deleteById(detailId);
    }

    @Transactional
    public void updateInvoiceQuantity(Integer detailId, Integer quantity) {
        GioHangChiTiet detail = gioHangChiTietRepository.findById(detailId).orElseThrow();
        
        // Kiểm tra tồn kho
        if (detail.getSanPhamChiTiet().getSoLuong() < quantity) {
            throw new RuntimeException("Số lượng vượt quá tồn kho hiện có (" + detail.getSanPhamChiTiet().getSoLuong() + ")");
        }

        detail.setSoLuong(quantity);
        gioHangChiTietRepository.save(detail);
    }

    @Transactional
    public void updateInvoiceCustomer(Integer id, Integer customerId) {
        // id là id_gio_hang
        GioHang gh = gioHangRepository.findById(id).orElseThrow();
        NguoiDung customer = nguoiDungRepository.findById(customerId).orElseThrow();
        gh.setNguoiDung(customer);
        gioHangRepository.save(gh);
    }

    public List<MaGiamGia> getApplicableVouchersForInvoice(Integer invoiceId) {
        GioHang gh = gioHangRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(invoiceId);
        BigDecimal subTotal = calculateInvoiceSubtotal(items);

        if (subTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return java.util.Collections.emptyList();
        }

        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        return maGiamGiaRepository.findByTrangThaiTrue().stream()
                .filter(v -> v.getNgayBatDau() != null && v.getNgayKetHuc() != null && !now.isBefore(v.getNgayBatDau()) && !now.isAfter(v.getNgayKetHuc()))
                .filter(v -> v.getSoLuong() == null || v.getSoLuongDaDung() == null || v.getSoLuong() > v.getSoLuongDaDung())
                .filter(v -> v.getGiaTriToiThieu() == null || subTotal.compareTo(v.getGiaTriToiThieu()) >= 0)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void applyVoucher(Integer id, String voucherCode) {
        MaGiamGia voucher = maGiamGiaRepository.findByMaCode(voucherCode)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (voucher.getNgayBatDau() != null && now.isBefore(voucher.getNgayBatDau())) {
            throw new RuntimeException("Voucher chưa hiệu lực");
        }
        if (voucher.getNgayKetHuc() != null && now.isAfter(voucher.getNgayKetHuc())) {
            throw new RuntimeException("Voucher đã hết hạn");
        }
        if (voucher.getSoLuong() != null && voucher.getSoLuongDaDung() != null && voucher.getSoLuong() <= voucher.getSoLuongDaDung()) {
            throw new RuntimeException("Voucher đã hết lượt sử dụng");
        }

        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(id);
        BigDecimal subTotal = calculateInvoiceSubtotal(items);

        if (subTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Voucher chỉ áp dụng cho sản phẩm đang trong đợt giảm giá");
        }

        if (voucher.getGiaTriToiThieu() != null && subTotal.compareTo(voucher.getGiaTriToiThieu()) < 0) {
            throw new RuntimeException("Tổng giá trị hàng giảm giá chưa đạt điều kiện tối thiểu của voucher");
        }

        // Không cập nhật data trên server để nếu cần persis có thể thêm trường vào GioHang.
    }

    @Transactional
    public void checkoutPOS(Integer id, Integer paymentMethodId, String note, Integer customerId, String voucherCode) {
        // id là id_gio_hang
        GioHang gh = gioHangRepository.findById(id).orElseThrow();
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(id);
        if (items.isEmpty()) throw new RuntimeException("Giỏ hàng trống!");

        PtThanhToan pttt = ptThanhToanRepository.findById(paymentMethodId).orElseThrow();
        NguoiDung customer = (customerId != null) ? nguoiDungRepository.findById(customerId).orElseThrow() : null;
        BigDecimal tongTienHang = calculateInvoiceSubtotal(items);
        MaGiamGia voucher = null;
        BigDecimal tienGiam = BigDecimal.ZERO;
        if (voucherCode != null && !voucherCode.isBlank()) {
            voucher = maGiamGiaRepository.findByMaCode(voucherCode)
                    .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
            if (voucher.getNgayBatDau() != null && LocalDateTime.now().isBefore(voucher.getNgayBatDau())) {
                throw new RuntimeException("Voucher chưa hiệu lực");
            }
            if (voucher.getNgayKetHuc() != null && LocalDateTime.now().isAfter(voucher.getNgayKetHuc())) {
                throw new RuntimeException("Voucher đã hết hạn");
            }
            if (voucher.getSoLuong() != null && voucher.getSoLuongDaDung() != null && voucher.getSoLuong() <= voucher.getSoLuongDaDung()) {
                throw new RuntimeException("Voucher đã hết lượt sử dụng");
            }
            if (voucher.getGiaTriToiThieu() != null && tongTienHang.compareTo(voucher.getGiaTriToiThieu()) < 0) {
                throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu để áp dụng voucher");
            }
            if ("PERCENT".equals(voucher.getKieuGiamGia())) {
                tienGiam = tongTienHang.multiply(voucher.getGiaTriGiam() != null ? voucher.getGiaTriGiam() : BigDecimal.ZERO)
                        .divide(BigDecimal.valueOf(100));
                if (voucher.getGiaTriGiamToiDa() != null && tienGiam.compareTo(voucher.getGiaTriGiamToiDa()) > 0) {
                    tienGiam = voucher.getGiaTriGiamToiDa();
                }
            } else {
                tienGiam = voucher.getGiaTriGiam() != null ? voucher.getGiaTriGiam() : BigDecimal.ZERO;
            }
            tienGiam = tienGiam.min(tongTienHang).max(BigDecimal.ZERO);
        }

        // B1: Tạo HoaDon thật (DA_GIAO)
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon("HD-" + System.currentTimeMillis());
        hd.setNguoiDung(customer != null ? customer : gh.getNguoiDung()); // Nếu không có khách thì gán cho staff
        hd.setTenNguoiNhan(customer != null ? customer.getHoTen() : "Khách lẻ");
        hd.setSoDienThoai(customer != null ? customer.getSoDienThoai() : "0000000000");
        hd.setPtThanhToan(pttt);
        hd.setTrangThaiDon("DA_GIAO");
        hd.setLoaiDonHang("TAI_QUAY");
        hd.setGhiChu(note);
        hd.setNgayCapNhat(java.time.LocalDateTime.now());
        hd.setTongTienHang(tongTienHang);
        hd.setTienGiam(tienGiam);
        hd.setTongThanhToan(tongTienHang.subtract(tienGiam).max(BigDecimal.ZERO));
        hd.setMaGiamGia(voucher);
        
        // Các giá trị tiền sẽ được Trigger trg_update_tong_tien_hd tự tính khi chèn chi tiết
        hd = hoaDonRepository.save(hd);

        // Chuyển data sang HoaDonChiTiet
        for (GioHangChiTiet item : items) {
            HoaDonChiTiet detail = new HoaDonChiTiet();
            detail.setHoaDon(hd);
            detail.setSanPhamChiTiet(item.getSanPhamChiTiet());
            detail.setSoLuong(item.getSoLuong());
            detail.setDonGia(item.getDonGia());
            detail.setThanhTien(item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong())));
            detail.setTenSanPham(item.getSanPhamChiTiet().getSanPham().getTenSanPham());
            detail.setKichThuoc(item.getSanPhamChiTiet().getKichThuoc().getTen());
            detail.setMauSac(item.getSanPhamChiTiet().getMauSac().getTen());
            detail.setChatLieu(item.getSanPhamChiTiet().getChatLieu().getTen());
            detail.setMaHoaDonChiTiet("HDCT-" + System.currentTimeMillis());
            hoaDonChiTietRepository.save(detail);
        }

        hd.setTongTienHang(tongTienHang);
        hd.setTienGiam(tienGiam);
        hd.setTongThanhToan(tongTienHang.subtract(tienGiam).max(BigDecimal.ZERO));
        hd.setMaGiamGia(voucher);
        hd = hoaDonRepository.save(hd);

        if (voucher != null) {
            int usedCount = voucher.getSoLuongDaDung() != null ? voucher.getSoLuongDaDung() : 0;
            voucher.setSoLuongDaDung(usedCount + 1);
            maGiamGiaRepository.save(voucher);
        }

        // B2: Đổi trạng thái giỏ hàng
        gh.setTrangThai("DA_THANH_TOAN");
        gioHangRepository.save(gh);

        // B3: Lưu lịch sử thanh toán
        LichSuThanhToan lstt = new LichSuThanhToan();
        lstt.setHoaDon(hd);
        lstt.setPtThanhToan(pttt);
        lstt.setSoTien(hd.getTongThanhToan()); // Trigger đã tính toán xong
        lstt.setTrangThaiThanhToan("DA_THANH_TOAN");
        lstt.setMaLichSuThanhToan("PAY-" + System.currentTimeMillis());
        lichSuThanhToanRepository.save(lstt);

        // Log history
        LichSuHoaDon lshd = new LichSuHoaDon();
        lshd.setHoaDon(hd);
        lshd.setTrangThai("DA_GIAO");
        lshd.setGhiChu("Thanh toán tại quầy - POS");
        lichSuHoaDonRepository.save(lshd);
    }

    @Transactional
    public void deleteWaitingInvoice(Integer id) {
        // Xóa tất cả chi tiết giỏ hàng trước để tránh lỗi khóa ngoại
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(id);
        if (!items.isEmpty()) {
            gioHangChiTietRepository.deleteAll(items);
        }
        // Sau đó xóa giỏ hàng
        gioHangRepository.deleteById(id);
    }

    private BigDecimal calculateInvoiceSubtotal(List<GioHangChiTiet> items) {
        return items.stream()
                .map(item -> {
                    BigDecimal donGia = item.getDonGia() != null ? item.getDonGia() : BigDecimal.ZERO;
                    int soLuong = item.getSoLuong() != null ? item.getSoLuong() : 0;
                    return donGia.multiply(BigDecimal.valueOf(soLuong));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --- Attributes ---
    public List<PtThanhToan> getAllPaymentMethods() { return ptThanhToanRepository.findAll(); }
    public List<MauSac> getAllColors() { return mauSacRepository.findAll(); }
    public List<KichThuoc> getAllSizes() { return kichThuocRepository.findAll(); }
    public List<ChatLieu> getAllMaterials() { return chatLieuRepository.findAll(); }

    public MauSac saveColor(MauSac color) {
        if (color.getMa() == null || color.getMa().isBlank()) {
            color.setMa(generateAttributeCode(color.getTen()));
        }
        return mauSacRepository.save(color);
    }

    public KichThuoc saveSize(KichThuoc size) {
        if (size.getMa() == null || size.getMa().isBlank()) {
            size.setMa(generateAttributeCode(size.getTen()));
        }
        return kichThuocRepository.save(size);
    }

    public ChatLieu saveMaterial(ChatLieu material) { return chatLieuRepository.save(material); }

    private String generateAttributeCode(String value) {
        if (value == null || value.isBlank()) {
            return "ATTR-" + System.currentTimeMillis();
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .replaceAll("[^A-Za-z0-9]+", "_")
                .replaceAll("^_+|_+$", "")
                .toUpperCase();

        return normalized.isBlank() ? "ATTR-" + System.currentTimeMillis() : normalized;
    }
}
