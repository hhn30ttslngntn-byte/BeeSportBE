package com.example.sport_be.service;

import com.example.sport_be.entity.*;
import com.example.sport_be.dto.DoiTraRequest;
import com.example.sport_be.dto.DoiTraChiTietRequest;
import com.example.sport_be.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final VaiTroRepository vaiTroRepository;
    private final DoiTraRepository doiTraRepository;
    private final DoiTraChiTietRepository doiTraChiTietRepository;
    private final DoiTraService doiTraService;
    private final EntityManager entityManager;

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
            // Tìm đợt giảm giá đang hoạt động cho sản phẩm này (dựa trên các biến thể của nó)
            GiamGiaSanPham activeGG = giamGiaSanPhamRepository.findAll().stream()
                    .filter(gg -> gg.getSanPhamChiTiet() != null && gg.getSanPhamChiTiet().getSanPham().getId().equals(sp.getId()))
                    .filter(gg -> gg.getDotGiamGia() != null && Boolean.TRUE.equals(gg.getDotGiamGia().getTrangThai()))
                    .filter(gg -> gg.getDotGiamGia().getNgayBatDau() != null && gg.getDotGiamGia().getNgayBatDau().isBefore(now))
                    .filter(gg -> gg.getDotGiamGia().getNgayKetHuc() != null && gg.getDotGiamGia().getNgayKetHuc().isAfter(now))
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
                .filter(gg -> gg.getSanPhamChiTiet() != null && gg.getSanPhamChiTiet().getSanPham().getId().equals(sp.getId()))
                .filter(gg -> gg.getDotGiamGia() != null && Boolean.TRUE.equals(gg.getDotGiamGia().getTrangThai()))
                .filter(gg -> gg.getDotGiamGia().getNgayBatDau() != null && gg.getDotGiamGia().getNgayBatDau().isBefore(now))
                .filter(gg -> gg.getDotGiamGia().getNgayKetHuc() != null && gg.getDotGiamGia().getNgayKetHuc().isAfter(now))
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
        for (SanPhamChiTiet v : details) {
            v.setGiaSauGiam(getPromotionPriceForVariant(v));
        }

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
        if (sanPham.getChatLieu() != null && sanPham.getChatLieu().getId() != null) {
            sanPham.setChatLieu(chatLieuRepository.findById(sanPham.getChatLieu().getId()).orElse(null));
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
        List<SanPhamChiTiet> details = sanPhamChiTietRepository.findAll();
        for (SanPhamChiTiet v : details) {
            v.setGiaSauGiam(getPromotionPriceForVariant(v));
        }
        return details;
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

        // Prevent duplicate brand
        if (brand.getId() == null) {
            if (thuongHieuRepository.findByMaThuongHieu(brand.getMaThuongHieu()).isPresent()) {
                throw new RuntimeException("Thương hiệu '" + brand.getTenThuongHieu() + "' đã tồn tại!");
            }
        } else {
            ThuongHieu existing = thuongHieuRepository.findByMaThuongHieu(brand.getMaThuongHieu()).orElse(null);
            if (existing != null && !existing.getId().equals(brand.getId())) {
                throw new RuntimeException("Mã thương hiệu '" + brand.getMaThuongHieu() + "' đã tồn tại!");
            }
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
        if (user.getId() == null) {
            // Chỉ bắt buộc các trường này khi là thêm mới một TÀI KHOẢN đầy đủ (Nhân viên/Khách hàng đăng ký)
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new RuntimeException("Email là bắt buộc khi tạo tài khoản!");
            }
            if (user.getMatKhau() == null || user.getMatKhau().isBlank()) {
                throw new RuntimeException("Mật khẩu là bắt buộc khi tạo tài khoản!");
            }
            if (user.getHoTen() == null || user.getHoTen().isBlank()) {
                throw new RuntimeException("Họ tên là bắt buộc khi tạo tài khoản!");
            }
            if (user.getSoDienThoai() == null || user.getSoDienThoai().isBlank()) {
                throw new RuntimeException("Số điện thoại là bắt buộc khi tạo tài khoản!");
            }
        }

        // Tự động sinh mã người dùng nếu chưa có (Tự nhảy mã)
        if (user.getMaNguoiDung() == null || user.getMaNguoiDung().isBlank()) {
            String prefix = "USER";
            if (user.getVaiTro() != null) {
                String roleName = user.getVaiTro().getTen() != null ? user.getVaiTro().getTen().toUpperCase() : "";
                if (roleName.contains("CUSTOMER") || roleName.contains("KHÁCH")) {
                    prefix = "KH";
                } else if (roleName.contains("ADMIN") || roleName.contains("STAFF") || roleName.contains("NHÂN VIÊN")) {
                    prefix = "NV";
                }
            }
            user.setMaNguoiDung(prefix + System.currentTimeMillis());
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
        LocalDate today = LocalDate.now();
        return getRevenueSummary("day", "revenue", "ALL", today.minusDays(29), today);
    }

    public Map<String, Object> getRevenueSummary(String mode, String metric, String orderType, LocalDate from, LocalDate to) {
        String normalizedMode = mode == null ? "day" : mode.trim().toLowerCase();
        if (!List.of("day", "month", "year").contains(normalizedMode)) {
            throw new RuntimeException("mode must be one of: day, month, year");
        }

        String normalizedMetric = metric == null ? "revenue" : metric.trim().toLowerCase();
        if (!List.of("quantity", "revenue").contains(normalizedMetric)) {
            throw new RuntimeException("metric must be one of: quantity, revenue");
        }

        String normalizedOrderType = normalizeOrderType(orderType);

        LocalDate today = LocalDate.now();
        LocalDate rangeFrom = from == null ? today.minusDays(29) : from;
        LocalDate rangeTo = to == null ? today : to;
        if (rangeFrom.isAfter(rangeTo)) {
            throw new RuntimeException("from must be less than or equal to to");
        }

        List<HoaDon> allBills = hoaDonRepository.findAll().stream()
                .filter(bill -> bill.getNgayTao() != null)
                .toList();

        List<HoaDon> deliveredBills = allBills.stream()
                .filter(this::isRevenueRecognizedBill)
                .toList();

        List<HoaDon> filteredBills = filterBillsByOrderType(deliveredBills, normalizedOrderType);
        List<HoaDon> filteredAllBills = filterBillsByOrderType(allBills, normalizedOrderType);

        Map<String, Object> overview = buildOverviewSummary(today, filteredBills);
        Map<String, Object> chart = buildChartData(normalizedMode, normalizedMetric, rangeFrom, rangeTo, filteredBills);
        Map<String, Object> orderStatus = buildOrderStatusData(rangeFrom, rangeTo, filteredAllBills);
        Map<String, Object> channelSummary = buildChannelSummary(rangeFrom, rangeTo, deliveredBills);
        List<Map<String, Object>> employeeStats = buildEmployeeStats(rangeFrom, rangeTo, filteredAllBills);
        List<Map<String, Object>> topProducts = buildTopProducts(rangeFrom, rangeTo, filteredBills);
        List<Map<String, Object>> topBuyers = buildTopBuyers(rangeFrom, rangeTo, filteredBills);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("todayRevenue", ((Map<?, ?>) overview.get("today")).get("revenue"));
        stats.put("todayOrders", filteredBills.stream()
                .filter(bill -> today.equals(bill.getNgayTao().toLocalDate()))
                .count());
        stats.put("todayProductsSold", filteredBills.stream()
                .filter(bill -> today.equals(bill.getNgayTao().toLocalDate()))
                .mapToInt(this::calculateDeliveredQuantity)
                .sum());

        List<Map<String, Object>> dailyRevenue = buildChartData("day", "revenue", rangeFrom, rangeTo, filteredBills)
                .get("data") instanceof List<?> data ? (List<Map<String, Object>>) data : List.of();

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("from", rangeFrom);
        filters.put("to", rangeTo);
        filters.put("mode", normalizedMode);
        filters.put("metric", normalizedMetric);
        filters.put("orderType", normalizedOrderType);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("filters", filters);
        response.put("stats", stats);
        response.put("overview", overview);
        response.put("channelSummary", channelSummary);
        response.put("chart", chart);
        response.put("orderStatus", orderStatus);
        response.put("employeeStats", employeeStats);
        response.put("topProducts", topProducts);
        response.put("topBuyers", topBuyers);
        response.put("dailyRevenue", dailyRevenue);
        return response;
    }

    private String normalizeOrderType(String orderType) {
        if (orderType == null || orderType.isBlank()) {
            return "ALL";
        }

        String normalized = orderType.trim().toUpperCase();
        if (!List.of("ALL", "ONLINE", "TAI_QUAY").contains(normalized)) {
            throw new RuntimeException("orderType must be one of: ALL, ONLINE, TAI_QUAY");
        }
        return normalized;
    }

    private List<HoaDon> filterBillsByOrderType(List<HoaDon> bills, String orderType) {
        if ("ALL".equalsIgnoreCase(orderType)) {
            return bills;
        }
        return bills.stream()
                .filter(bill -> orderType.equalsIgnoreCase(bill.getLoaiDonHang()))
                .toList();
    }

    private Map<String, Object> buildOverviewSummary(LocalDate today, List<HoaDon> deliveredBills) {
        LocalDate yesterday = today.minusDays(1);
        BigDecimal todayRevenue = calculateRevenueByDateRange(deliveredBills, today, today);
        BigDecimal yesterdayRevenue = calculateRevenueByDateRange(deliveredBills, yesterday, yesterday);

        LocalDate thisWeekFrom = today.with(DayOfWeek.MONDAY);
        LocalDate thisWeekTo = today.with(DayOfWeek.SUNDAY);
        LocalDate lastWeekFrom = thisWeekFrom.minusWeeks(1);
        LocalDate lastWeekTo = thisWeekTo.minusWeeks(1);
        BigDecimal weekRevenue = calculateRevenueByDateRange(deliveredBills, thisWeekFrom, thisWeekTo);
        BigDecimal lastWeekRevenue = calculateRevenueByDateRange(deliveredBills, lastWeekFrom, lastWeekTo);

        LocalDate thisMonthFrom = today.withDayOfMonth(1);
        LocalDate thisMonthTo = today.withDayOfMonth(today.lengthOfMonth());
        LocalDate lastMonthBase = today.minusMonths(1);
        LocalDate lastMonthFrom = lastMonthBase.withDayOfMonth(1);
        LocalDate lastMonthTo = lastMonthBase.withDayOfMonth(lastMonthBase.lengthOfMonth());
        BigDecimal monthRevenue = calculateRevenueByDateRange(deliveredBills, thisMonthFrom, thisMonthTo);
        BigDecimal lastMonthRevenue = calculateRevenueByDateRange(deliveredBills, lastMonthFrom, lastMonthTo);

        Map<String, Object> todayCard = new LinkedHashMap<>();
        todayCard.put("revenue", todayRevenue);
        todayCard.put("changePercent", calculatePercentChange(todayRevenue, yesterdayRevenue));
        todayCard.put("compareBaseRevenue", yesterdayRevenue);

        Map<String, Object> weekCard = new LinkedHashMap<>();
        weekCard.put("revenue", weekRevenue);
        weekCard.put("changePercent", calculatePercentChange(weekRevenue, lastWeekRevenue));
        weekCard.put("compareBaseRevenue", lastWeekRevenue);

        Map<String, Object> monthCard = new LinkedHashMap<>();
        monthCard.put("revenue", monthRevenue);
        monthCard.put("changePercent", calculatePercentChange(monthRevenue, lastMonthRevenue));
        monthCard.put("compareBaseRevenue", lastMonthRevenue);

        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("today", todayCard);
        overview.put("week", weekCard);
        overview.put("month", monthCard);
        return overview;
    }

    private Map<String, Object> buildChartData(String mode, String metric, LocalDate from, LocalDate to, List<HoaDon> deliveredBills) {
        Map<String, List<HoaDon>> grouped = new LinkedHashMap<>();

        if ("year".equals(mode)) {
            for (int year = from.getYear(); year <= to.getYear(); year++) {
                grouped.put(String.valueOf(year), new ArrayList<>());
            }
            for (HoaDon bill : deliveredBills) {
                LocalDate billDate = bill.getNgayTao().toLocalDate();
                if (billDate.isBefore(from) || billDate.isAfter(to)) continue;
                grouped.computeIfAbsent(String.valueOf(billDate.getYear()), key -> new ArrayList<>()).add(bill);
            }
        } else if ("month".equals(mode)) {
            YearMonth start = YearMonth.from(from);
            YearMonth end = YearMonth.from(to);
            YearMonth cursor = start;
            while (!cursor.isAfter(end)) {
                grouped.put(cursor.toString(), new ArrayList<>());
                cursor = cursor.plusMonths(1);
            }
            for (HoaDon bill : deliveredBills) {
                LocalDate billDate = bill.getNgayTao().toLocalDate();
                if (billDate.isBefore(from) || billDate.isAfter(to)) continue;
                grouped.computeIfAbsent(YearMonth.from(billDate).toString(), key -> new ArrayList<>()).add(bill);
            }
        } else {
            LocalDate cursor = from;
            while (!cursor.isAfter(to)) {
                grouped.put(cursor.toString(), new ArrayList<>());
                cursor = cursor.plusDays(1);
            }
            for (HoaDon bill : deliveredBills) {
                LocalDate billDate = bill.getNgayTao().toLocalDate();
                if (billDate.isBefore(from) || billDate.isAfter(to)) continue;
                grouped.computeIfAbsent(billDate.toString(), key -> new ArrayList<>()).add(bill);
            }
        }

        List<Map<String, Object>> data = grouped.entrySet().stream()
                .map(entry -> {
                    List<HoaDon> bucket = entry.getValue();
                    BigDecimal revenue = bucket.stream()
                            .map(this::calculateProductRevenue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    int quantity = bucket.stream().mapToInt(this::calculateDeliveredQuantity).sum();

                    Map<String, Object> point = new LinkedHashMap<>();
                    point.put("label", entry.getKey());
                    point.put("date", entry.getKey());
                    point.put("orders", bucket.size());
                    point.put("productsSold", quantity);
                    point.put("quantity", quantity);
                    point.put("revenue", revenue);
                    point.put("value", "revenue".equals(metric) ? revenue : BigDecimal.valueOf(quantity));
                    return point;
                })
                .toList();

        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("mode", mode);
        chart.put("metric", metric);
        chart.put("data", data);
        return chart;
    }

    private Map<String, Object> buildOrderStatusData(LocalDate from, LocalDate to, List<HoaDon> allBills) {
        List<HoaDon> inRange = allBills.stream()
                .filter(bill -> {
                    LocalDate billDate = bill.getNgayTao().toLocalDate();
                    return !billDate.isBefore(from) && !billDate.isAfter(to);
                })
                .toList();

        Map<String, Integer> grouped = new LinkedHashMap<>();
        grouped.put("HUY_GIAO_DICH", 0);
        grouped.put("DANG_XU_LY", 0);
        grouped.put("THANH_CONG", 0);
        grouped.put("DA_HUY", 0);

        for (HoaDon bill : inRange) {
            String groupKey = mapStatusGroup(bill.getTrangThaiDon());
            grouped.put(groupKey, grouped.get(groupKey) + 1);
        }

        List<Map<String, Object>> items = new ArrayList<>();
        items.add(buildStatusItem("HUY_GIAO_DICH", "Hủy giao dịch", grouped.get("HUY_GIAO_DICH"), "#ef4444"));
        items.add(buildStatusItem("DANG_XU_LY", "Đang xử lý", grouped.get("DANG_XU_LY"), "#4f46e5"));
        items.add(buildStatusItem("THANH_CONG", "Thành công", grouped.get("THANH_CONG"), "#22c55e"));
        items.add(buildStatusItem("DA_HUY", "Đã hủy", grouped.get("DA_HUY"), "#38bdf8"));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalOrders", inRange.size());
        response.put("items", items);
        return response;
    }

    private Map<String, Object> buildChannelSummary(LocalDate from, LocalDate to, List<HoaDon> deliveredBills) {
        List<HoaDon> inRange = deliveredBills.stream()
                .filter(bill -> {
                    LocalDate billDate = bill.getNgayTao().toLocalDate();
                    return !billDate.isBefore(from) && !billDate.isAfter(to);
                })
                .toList();

        List<HoaDon> onlineBills = inRange.stream()
                .filter(bill -> "ONLINE".equalsIgnoreCase(bill.getLoaiDonHang()))
                .toList();

        List<HoaDon> posBills = inRange.stream()
                .filter(bill -> "TAI_QUAY".equalsIgnoreCase(bill.getLoaiDonHang()))
                .toList();

        BigDecimal onlineRevenue = onlineBills.stream()
                .map(this::calculateOnlineRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal posRevenue = posBills.stream()
                .map(this::calculatePosRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> online = new LinkedHashMap<>();
        online.put("revenue", onlineRevenue);
        online.put("orders", onlineBills.size());

        Map<String, Object> pos = new LinkedHashMap<>();
        pos.put("revenue", posRevenue);
        pos.put("orders", posBills.size());

        Map<String, Object> total = new LinkedHashMap<>();
        total.put("revenue", onlineRevenue.add(posRevenue));
        total.put("orders", inRange.size());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("online", online);
        result.put("pos", pos);
        result.put("total", total);
        return result;
    }

    private Map<String, Object> buildStatusItem(String key, String label, Integer count, String color) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("key", key);
        item.put("label", label);
        item.put("count", count);
        item.put("color", color);
        return item;
    }

    private List<Map<String, Object>> buildTopProducts(LocalDate from, LocalDate to, List<HoaDon> deliveredBills) {
        List<HoaDon> inRange = deliveredBills.stream()
                .filter(bill -> {
                    LocalDate billDate = bill.getNgayTao().toLocalDate();
                    return !billDate.isBefore(from) && !billDate.isAfter(to);
                })
                .toList();

        Set<Integer> billIds = inRange.stream()
                .map(HoaDon::getId)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());
        if (billIds.isEmpty()) {
            return List.of();
        }

        Map<Integer, Map<String, Object>> productStats = new LinkedHashMap<>();
        for (HoaDonChiTiet detail : hoaDonChiTietRepository.findAll()) {
            if (detail == null || detail.getHoaDon() == null || detail.getHoaDon().getId() == null) {
                continue;
            }
            if (!billIds.contains(detail.getHoaDon().getId())) {
                continue;
            }

            Integer productId = detail.getSanPhamChiTiet() != null && detail.getSanPhamChiTiet().getSanPham() != null
                    ? detail.getSanPhamChiTiet().getSanPham().getId()
                    : null;
            if (productId == null) {
                continue;
            }

            String productName = detail.getTenSanPham();
            if ((productName == null || productName.isBlank())
                    && detail.getSanPhamChiTiet() != null
                    && detail.getSanPhamChiTiet().getSanPham() != null) {
                productName = detail.getSanPhamChiTiet().getSanPham().getTenSanPham();
            }
            final String displayName = (productName == null || productName.isBlank()) ? ("SP #" + productId) : productName;

            Map<String, Object> stats = productStats.computeIfAbsent(productId, key -> {
                Map<String, Object> init = new LinkedHashMap<>();
                init.put("productId", productId);
                init.put("productName", displayName);
                init.put("totalQuantity", 0);
                init.put("totalRevenue", BigDecimal.ZERO);
                return init;
            });

            int quantity = detail.getSoLuong() == null ? 0 : detail.getSoLuong();
            BigDecimal lineRevenue = detail.getThanhTien();
            if (lineRevenue == null) {
                BigDecimal donGia = detail.getDonGia() == null ? BigDecimal.ZERO : detail.getDonGia();
                lineRevenue = donGia.multiply(BigDecimal.valueOf(quantity));
            }

            stats.put("totalQuantity", (Integer) stats.get("totalQuantity") + quantity);
            stats.put("totalRevenue", ((BigDecimal) stats.get("totalRevenue")).add(lineRevenue));
        }

        return productStats.values().stream()
                .sorted((left, right) -> {
                    int revenueCompare = ((BigDecimal) right.get("totalRevenue")).compareTo((BigDecimal) left.get("totalRevenue"));
                    if (revenueCompare != 0) return revenueCompare;
                    return Integer.compare((Integer) right.get("totalQuantity"), (Integer) left.get("totalQuantity"));
                })
                .limit(5)
                .toList();
    }

    private List<Map<String, Object>> buildTopBuyers(LocalDate from, LocalDate to, List<HoaDon> deliveredBills) {
        Map<Integer, Map<String, Object>> buyerStats = new LinkedHashMap<>();

        for (HoaDon bill : deliveredBills) {
            if (bill.getNgayTao() == null || bill.getNguoiDung() == null || bill.getNguoiDung().getId() == null) {
                continue;
            }
            LocalDate billDate = bill.getNgayTao().toLocalDate();
            if (billDate.isBefore(from) || billDate.isAfter(to)) {
                continue;
            }

            Integer userId = bill.getNguoiDung().getId();
            Map<String, Object> stats = buyerStats.computeIfAbsent(userId, key -> {
                Map<String, Object> init = new LinkedHashMap<>();
                init.put("userId", userId);
                init.put("buyerName", bill.getNguoiDung().getHoTen() == null || bill.getNguoiDung().getHoTen().isBlank()
                        ? ("KH #" + userId)
                        : bill.getNguoiDung().getHoTen());
                init.put("totalOrders", 0);
                init.put("totalSpent", BigDecimal.ZERO);
                return init;
            });

            stats.put("totalOrders", (Integer) stats.get("totalOrders") + 1);
            stats.put("totalSpent", ((BigDecimal) stats.get("totalSpent")).add(calculateProductRevenue(bill)));
        }

        return buyerStats.values().stream()
                .sorted((left, right) -> {
                    int spentCompare = ((BigDecimal) right.get("totalSpent")).compareTo((BigDecimal) left.get("totalSpent"));
                    if (spentCompare != 0) return spentCompare;
                    return Integer.compare((Integer) right.get("totalOrders"), (Integer) left.get("totalOrders"));
                })
                .limit(5)
                .toList();
    }

    private List<Map<String, Object>> buildEmployeeStats(LocalDate from, LocalDate to, List<HoaDon> allBills) {
        Map<Integer, Map<String, Object>> byEmployee = new LinkedHashMap<>();

        for (HoaDon bill : allBills) {
            if (bill.getNgayTao() == null || bill.getNguoiDung() == null || bill.getNguoiDung().getId() == null) {
                continue;
            }

            LocalDate billDate = bill.getNgayTao().toLocalDate();
            if (billDate.isBefore(from) || billDate.isAfter(to)) {
                continue;
            }

            NguoiDung account = bill.getNguoiDung();
            String roleCode = account.getVaiTro() != null ? account.getVaiTro().getMa() : null;
            if (!"ADMIN".equalsIgnoreCase(roleCode) && !"STAFF".equalsIgnoreCase(roleCode)) {
                continue;
            }

            Integer employeeId = account.getId();
            Map<String, Object> stats = byEmployee.computeIfAbsent(employeeId, key -> {
                Map<String, Object> init = new LinkedHashMap<>();
                init.put("employeeId", account.getId());
                init.put("employeeCode", account.getMaNguoiDung());
                init.put("employeeName", account.getHoTen());
                init.put("totalOrders", 0);
                init.put("successfulOrders", 0);
                init.put("revenue", BigDecimal.ZERO);
                return init;
            });

            stats.put("totalOrders", (Integer) stats.get("totalOrders") + 1);
            if (isRevenueRecognizedBill(bill)) {
                stats.put("successfulOrders", (Integer) stats.get("successfulOrders") + 1);
                stats.put("revenue", ((BigDecimal) stats.get("revenue")).add(calculateProductRevenue(bill)));
            }
        }

        return byEmployee.values().stream()
                .sorted((left, right) -> Integer.compare((Integer) right.get("totalOrders"), (Integer) left.get("totalOrders")))
                .toList();
    }

    private BigDecimal calculateRevenueByDateRange(List<HoaDon> bills, LocalDate from, LocalDate to) {
        return bills.stream()
                .filter(bill -> {
                    LocalDate billDate = bill.getNgayTao().toLocalDate();
                    return !billDate.isBefore(from) && !billDate.isAfter(to);
                })
                .map(this::calculateProductRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculatePercentChange(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return current.subtract(previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(previous, 2, RoundingMode.HALF_UP);
    }

    private int calculateDeliveredQuantity(HoaDon bill) {
        if (bill.getId() == null) {
            return 0;
        }
        return hoaDonChiTietRepository.findByHoaDonId(bill.getId()).stream()
                .map(HoaDonChiTiet::getSoLuong)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private boolean isRevenueRecognizedBill(HoaDon bill) {
        if (bill == null || bill.getTrangThaiDon() == null || bill.getTrangThaiDon().isBlank()) {
            return false;
        }

        String status = bill.getTrangThaiDon().trim().toUpperCase();
        String orderType = bill.getLoaiDonHang() == null ? "" : bill.getLoaiDonHang().trim().toUpperCase();

        if ("ONLINE".equals(orderType)) {
            return List.of("DA_THANH_TOAN", "DANG_GIAO", "DA_GIAO").contains(status);
        }
        if ("TAI_QUAY".equals(orderType)) {
            return "DA_GIAO".equals(status);
        }

        return "DA_GIAO".equals(status);
    }

    private String mapStatusGroup(String status) {
        if (status == null || status.isBlank()) {
            return "DANG_XU_LY";
        }
        return switch (status.trim().toUpperCase()) {
            case "HOAN_TRA", "HUY_GIAO_DICH" -> "HUY_GIAO_DICH";
            case "DA_GIAO" -> "THANH_CONG";
            case "DA_HUY" -> "DA_HUY";
            default -> "DANG_XU_LY";
        };
    }

    private BigDecimal calculateProductRevenue(HoaDon bill) {
        if ("ONLINE".equalsIgnoreCase(bill.getLoaiDonHang())) {
            return calculateOnlineRevenue(bill);
        }
        if ("TAI_QUAY".equalsIgnoreCase(bill.getLoaiDonHang())) {
            return calculatePosRevenue(bill);
        }

        BigDecimal tongTienHang = bill.getTongTienHang();
        BigDecimal tienGiam = bill.getTienGiam() != null ? bill.getTienGiam() : BigDecimal.ZERO;
        if (tongTienHang != null) {
            return tongTienHang.subtract(tienGiam).max(BigDecimal.ZERO);
        }

        BigDecimal tongThanhToan = bill.getTongThanhToan() != null ? bill.getTongThanhToan() : BigDecimal.ZERO;
        BigDecimal phiVanChuyen = bill.getPhiVanChuyen() != null ? bill.getPhiVanChuyen() : BigDecimal.ZERO;
        return tongThanhToan.subtract(phiVanChuyen).max(BigDecimal.ZERO);
    }

    private BigDecimal calculateOnlineRevenue(HoaDon bill) {
        BigDecimal tongThanhToan = bill.getTongThanhToan() != null ? bill.getTongThanhToan() : BigDecimal.ZERO;
        BigDecimal phiVanChuyen = bill.getPhiVanChuyen() != null ? bill.getPhiVanChuyen() : BigDecimal.ZERO;
        return tongThanhToan.subtract(phiVanChuyen).max(BigDecimal.ZERO);
    }

    private BigDecimal calculatePosRevenue(HoaDon bill) {
        BigDecimal tongThanhToan = bill.getTongThanhToan() != null ? bill.getTongThanhToan() : BigDecimal.ZERO;
        return tongThanhToan.max(BigDecimal.ZERO);
    }

    private static final Map<String, List<String>> ONLINE_STATUS_TRANSITIONS = Map.of(
            "CHO_XAC_NHAN", List.of("DA_XAC_NHAN", "DA_HUY"),
            "DA_XAC_NHAN", List.of("DANG_GIAO", "DA_HUY"),
            "DANG_GIAO", List.of("DA_GIAO"),
            "DA_GIAO", List.of("HOAN_TRA"),
            "DA_HUY", List.of(),
            "HOAN_TRA", List.of()
    );

    private static final Map<String, List<String>> POS_STATUS_TRANSITIONS = Map.of(
            "DA_GIAO", List.of("HOAN_TRA"),
            "DA_HUY", List.of(),
            "HOAN_TRA", List.of()
    );

    @Transactional
    public HoaDon updateBillStatus(Integer id, String status) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        String normalizedStatus = status == null ? null : status.trim();
        if (normalizedStatus == null || normalizedStatus.isEmpty()) {
            throw new RuntimeException("Status is required");
        }

        String oldStatus = hoaDon.getTrangThaiDon();
        if (normalizedStatus.equals(oldStatus)) {
            return hoaDon;
        }

        List<String> allowedStatuses = getStatusTransitions(hoaDon).get(oldStatus);
        if (allowedStatuses == null) {
            throw new RuntimeException("Unsupported current status: " + oldStatus);
        }
        if (!allowedStatuses.contains(normalizedStatus)) {
            throw new RuntimeException("Cannot move status from " + oldStatus + " to " + normalizedStatus);
        }

        if ("DANG_GIAO".equals(normalizedStatus)) {
            hoaDon.setNgayGiao(java.time.LocalDateTime.now());
        }

        hoaDon.setTrangThaiDon(normalizedStatus);
        HoaDon updated = hoaDonRepository.save(hoaDon);

        // Log history
        LichSuHoaDon history = new LichSuHoaDon();
        history.setHoaDon(updated);
        history.setTrangThaiCu(oldStatus);
        history.setTrangThaiMoi(normalizedStatus);
        history.setLoaiHanhDong("UPDATE_STATUS");
        history.setHanhDong("Cập nhật trạng thái từ " + oldStatus + " sang " + normalizedStatus);
        lichSuHoaDonRepository.save(history);

        return updated;
    }

    public Object getBillDetail(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
        List<HoaDonChiTiet> items = hoaDonChiTietRepository.findByHoaDonId(id);
        List<LichSuHoaDon> history = lichSuHoaDonRepository.findByHoaDonIdOrderByThoiGianDesc(id);

        Map<String, Object> response = new HashMap<>();
        response.put("bill", toBillDetailMap(hoaDon));
        response.put("items", items.stream().map(this::toBillItemMap).toList());
        response.put("history", history.stream().map(this::toBillHistoryMap).toList());
        return response;
    }

    private Map<String, Object> toBillSummaryMap(HoaDon hoaDon) {
        Map<String, Object> bill = new LinkedHashMap<>();
        bill.put("id", hoaDon.getId());
        bill.put("maHoaDon", hoaDon.getMaHoaDon());
        bill.put("tenNguoiNhan", preferCurrentValue(hoaDon.getTenNguoiNhan(),
                hoaDon.getNguoiDung() != null ? hoaDon.getNguoiDung().getHoTen() : null));
        bill.put("tenKhachHang", sanitizeDisplayText(
                hoaDon.getNguoiDung() != null ? hoaDon.getNguoiDung().getHoTen() : hoaDon.getTenNguoiNhan()
        ));
        bill.put("soDienThoai", sanitizeDisplayText(hoaDon.getSoDienThoai()));
        bill.put("tongTienHang", hoaDon.getTongTienHang());
        bill.put("tienGiam", hoaDon.getTienGiam());
        bill.put("phiVanChuyen", hoaDon.getPhiVanChuyen());
        bill.put("tongThanhToan", hoaDon.getTongThanhToan());
        bill.put("trangThaiDon", hoaDon.getTrangThaiDon());
        bill.put("availableNextStatuses", getAvailableNextStatuses(hoaDon));
        bill.put("loaiDonHang", hoaDon.getLoaiDonHang());
        bill.put("tenPttt", hoaDon.getPtThanhToan() != null ? hoaDon.getPtThanhToan().getTenPttt() : "Chưa xác định");
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
        bill.put("tenNguoiNhan", preferCurrentValue(hoaDon.getTenNguoiNhan(),
                hoaDon.getNguoiDung() != null ? hoaDon.getNguoiDung().getHoTen() : null));
        bill.put("tenKhachHang", sanitizeDisplayText(
                hoaDon.getNguoiDung() != null ? hoaDon.getNguoiDung().getHoTen() : hoaDon.getTenNguoiNhan()
        ));
        bill.put("maKhachHang", hoaDon.getNguoiDung() != null ? hoaDon.getNguoiDung().getMaNguoiDung() : null);
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
        bill.put("tenPttt", hoaDon.getPtThanhToan() != null ? hoaDon.getPtThanhToan().getTenPttt() : "Chưa xác định");
        bill.put("availableNextStatuses", getAvailableNextStatuses(hoaDon));
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

        if (addresses == null || addresses.isEmpty()) {
            return null;
        }

        return addresses.stream()
                .filter(item -> item != null && sameText(item.getTenNguoiNhan(), hoaDon.getTenNguoiNhan()))
                .filter(item -> item != null && sameText(item.getSoDienThoai(), hoaDon.getSoDienThoai()))
                .filter(item -> item != null && sameText(item.getDiaChiChiTiet(), hoaDon.getDiaChiChiTiet()))
                .findFirst()
                .orElseGet(() -> addresses.stream()
                        .filter(item -> item != null && sameText(item.getDiaChiChiTiet(), hoaDon.getDiaChiChiTiet()))
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
        result.put("chatLieu", preferCurrentValue(item.getChatLieu(), sanPham != null && sanPham.getChatLieu() != null ? sanPham.getChatLieu().getTen() : null));
        result.put("donGia", item.getDonGia());
        result.put("soLuong", item.getSoLuong());
        result.put("thanhTien", item.getThanhTien());
        return result;
    }

    private Map<String, Object> toBillHistoryMap(LichSuHoaDon history) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", history.getId());
        result.put("maLichSu", history.getMaLichSu());
        result.put("trangThaiCu", history.getTrangThaiCu());
        result.put("trangThaiMoi", history.getTrangThaiMoi());
        result.put("loaiHanhDong", history.getLoaiHanhDong());
        result.put("hanhDong", history.getHanhDong());
        result.put("thoiGian", history.getThoiGian());
        result.put("nguoiThucHien", history.getNguoiThucHien() != null ? history.getNguoiThucHien().getHoTen() : null);
        return result;
    }

    private Map<String, List<String>> getStatusTransitions(HoaDon hoaDon) {
        if (hoaDon != null && "TAI_QUAY".equalsIgnoreCase(hoaDon.getLoaiDonHang())) {
            return POS_STATUS_TRANSITIONS;
        }
        return ONLINE_STATUS_TRANSITIONS;
    }

    private List<String> getAvailableNextStatuses(HoaDon hoaDon) {
        String currentStatus = hoaDon != null ? hoaDon.getTrangThaiDon() : null;
        if (currentStatus == null || currentStatus.isBlank()) {
            return List.of();
        }
        return getStatusTransitions(hoaDon).getOrDefault(currentStatus, List.of());
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
        // Fix common known broken snapshots
        if (value.trim().equalsIgnoreCase("Khach l?") || value.trim().equalsIgnoreCase("Khách l?")) {
            return "Khách lẻ";
        }
        return value;
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
    public DotGiamGia savePromotion(DotGiamGia promotion, List<Integer> spctIds) {
        DotGiamGia saved = dotGiamGiaRepository.save(promotion);

        // Update product promotions
        if (spctIds != null) {
            // Clear old
            giamGiaSanPhamRepository.deleteByDotGiamGiaId(saved.getId());

            // Add new
            for (Integer spctId : spctIds) {
                GiamGiaSanPham ggsp = new GiamGiaSanPham();
                ggsp.setDotGiamGia(saved);
                SanPhamChiTiet spct = new SanPhamChiTiet();
                spct.setId(spctId);
                ggsp.setSanPhamChiTiet(spct);
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
                .filter(ggsp -> ggsp.getSanPhamChiTiet() != null)
                .map(ggsp -> ggsp.getSanPhamChiTiet().getId())
                .collect(java.util.stream.Collectors.toList());
    }

    // --- POS Refined (Using gio_hang for waiting orders) ---
    public List<GioHang> getWaitingInvoices() {
        return gioHangRepository.findByLoaiGioHangAndTrangThai("TAI_QUAY", "DANG_SU_DUNG");
    }

    @Transactional
    public Object getWaitingInvoiceDetail(Integer id) {
        GioHang gh = gioHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn chờ"));
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(id);

        // Cập nhật lại giá hiện tại cho từng item (nếu có thay đổi giá hoặc khuyến mãi mới)
        for (GioHangChiTiet item : items) {
            BigDecimal currentPrice = getPromotionPriceForVariant(item.getSanPhamChiTiet());
            if (item.getDonGia() == null || item.getDonGia().compareTo(currentPrice) != 0) {
                item.setDonGia(currentPrice);
                gioHangChiTietRepository.save(item);
            }
        }

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

        // Sử dụng hàm có sẵn để lấy giá khuyến mãi chính xác
        BigDecimal donGia = getPromotionPriceForVariant(spct);

        // Sử dụng logic nativeAddToCart hoặc thủ công để tránh trùng lặp trong giỏ
        String ma = "GHCT-POS-" + System.currentTimeMillis();
        gioHangChiTietRepository.nativeAddToCart(id, spctId, quantity, donGia.max(BigDecimal.ZERO), ma);
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
    public NguoiDung getOrCreateRetailCustomer() {
        String retailPhone = "0000000000";
        List<NguoiDung> existing = nguoiDungRepository.findBySoDienThoai(retailPhone);
        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        NguoiDung retail = new NguoiDung();
        retail.setHoTen("Khách lẻ");
        retail.setSoDienThoai(retailPhone);
        retail.setTrangThai(true);
        retail.setMaNguoiDung("RETAIL" + System.currentTimeMillis());

        List<VaiTro> roles = vaiTroRepository.findByTen("CUSTOMER");
        if (roles.isEmpty()) {
            roles = vaiTroRepository.findByTen("Khách hàng");
        }

        if (!roles.isEmpty()) {
            retail.setVaiTro(roles.get(0));
        }
        return nguoiDungRepository.save(retail);
    }

    @Transactional
    public NguoiDung quickCreateCustomer(String hoTen, String soDienThoai) {
        // 1. Kiểm tra SĐT trước để tránh trùng lặp UNIQUE so_dien_thoai
        List<NguoiDung> existing = nguoiDungRepository.findBySoDienThoai(soDienThoai);
        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        NguoiDung customer = new NguoiDung();
        customer.setHoTen(hoTen);
        customer.setSoDienThoai(soDienThoai);
        customer.setTrangThai(true);

        // Tạo mã người dùng duy nhất dựa trên thời gian để tránh trùng lặp mã
        customer.setMaNguoiDung("KH" + System.currentTimeMillis());

        // Đảm bảo email và mật khẩu là null cho khách lẻ
        customer.setEmail(null);
        customer.setMatKhau(null);

        // 2. Xử lý Vai trò
        List<VaiTro> roles = vaiTroRepository.findByTen("CUSTOMER");
        if (roles.isEmpty()) {
            roles = vaiTroRepository.findByTen("Khách hàng");
        }

        if (!roles.isEmpty()) {
            customer.setVaiTro(roles.get(0));
        } else {
            List<VaiTro> allRoles = vaiTroRepository.findAll();
            if (!allRoles.isEmpty()) {
                customer.setVaiTro(allRoles.get(0));
            }
        }

        try {
            return nguoiDungRepository.save(customer);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu khách hàng: " + e.getMessage());
        }
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
        // Set user_id in session context for triggers (hardcoded for now as per other methods)
        entityManager.createNativeQuery("EXEC sp_set_session_context 'user_id', 1").executeUpdate();

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

        // B1: Tạo HoaDon thật (CHO_XAC_NHAN để trigger tính toán)
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon("HD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        NguoiDung effectiveCustomer = (customer != null) ? customer : gh.getNguoiDung();
        if (effectiveCustomer == null) {
            effectiveCustomer = getOrCreateRetailCustomer();
        }

        hd.setNguoiDung(effectiveCustomer);
        hd.setTenNguoiNhan(effectiveCustomer.getHoTen());
        hd.setSoDienThoai(effectiveCustomer.getSoDienThoai());
        hd.setPtThanhToan(pttt);
        hd.setTrangThaiDon("DA_GIAO");
        hd.setLoaiDonHang("TAI_QUAY");
        hd.setGhiChu(note);
        hd.setNgayCapNhat(java.time.LocalDateTime.now());
        hd.setTongTienHang(tongTienHang);
        hd.setTienGiam(tienGiam);
        hd.setTongThanhToan(tongTienHang.subtract(tienGiam).max(BigDecimal.ZERO));
        hd.setMaGiamGia(voucher);

        for (GioHangChiTiet item : items) {
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(item.getSanPhamChiTiet().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            int requestedQty = item.getSoLuong() != null ? item.getSoLuong() : 0;
            int availableQty = spct.getSoLuong() != null ? spct.getSoLuong() : 0;
            if (requestedQty <= 0) {
                throw new RuntimeException("Invalid product quantity");
            }
            if (requestedQty > availableQty) {
                throw new RuntimeException("Insufficient stock");
            }
        }

        // Lưu hóa đơn trước khi chèn chi tiết
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
            detail.setKichThuoc(item.getSanPhamChiTiet().getKichThuoc() != null ? item.getSanPhamChiTiet().getKichThuoc().getTen() : null);
            detail.setMauSac(item.getSanPhamChiTiet().getMauSac() != null ? item.getSanPhamChiTiet().getMauSac().getTen() : null);
            detail.setChatLieu(item.getSanPhamChiTiet().getSanPham().getChatLieu() != null ? item.getSanPhamChiTiet().getSanPham().getChatLieu().getTen() : null);
            detail.setMaHoaDonChiTiet("HDCT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            hoaDonChiTietRepository.save(detail);
        }


        for (GioHangChiTiet item : items) {
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(item.getSanPhamChiTiet().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            int requestedQty = item.getSoLuong() != null ? item.getSoLuong() : 0;
            int availableQty = spct.getSoLuong() != null ? spct.getSoLuong() : 0;
            if (requestedQty > availableQty) {
                throw new RuntimeException("Insufficient stock");
            }
            spct.setSoLuong(availableQty - requestedQty);
            int soldQty = spct.getSoLuongDaBan() != null ? spct.getSoLuongDaBan() : 0;
            spct.setSoLuongDaBan(soldQty + requestedQty);
            sanPhamChiTietRepository.save(spct);
        }

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
        lstt.setSoTien(hd.getTongThanhToan());
        lstt.setTrangThaiThanhToan("DA_THANH_TOAN");
        lstt.setMaLichSuThanhToan("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        lichSuThanhToanRepository.save(lstt);

        // Log history
        LichSuHoaDon lshd = new LichSuHoaDon();
        lshd.setHoaDon(hd);
        lshd.setTrangThaiMoi("DA_GIAO");
        lshd.setLoaiHanhDong("CREATE_BILL");
        lshd.setHanhDong("Thanh toán tại quầy - POS");
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
    // --- Đổi Trả (Admin ép tạo) ---

    /**
     * Admin tạo yêu cầu đổi trả thay khách từ màn Quản Lý Hóa Đơn.
     * Bỏ qua rule 7 ngày nhưng vẫn check số lượng trả hợp lệ.
     * Ảnh bắt buộc.
     */
    @Transactional
    public DoiTra createDoiTraByAdmin(DoiTraRequest request, MultipartFile[] files, String baseUrl) {
        return doiTraService.taoYeuCauDoiTra(request, files, baseUrl);
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
