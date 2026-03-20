package com.example.sport_be.service;

import com.example.sport_be.entity.*;
import com.example.sport_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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

    // --- Product ---
    public List<SanPham> getAllProducts() {
        return sanPhamRepository.findAll();
    }

    public SanPham getProductById(Integer id) {
        return sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Object getProductDetail(Integer id) {
        SanPham sp = getProductById(id);
        List<SanPhamChiTiet> details = sanPhamChiTietRepository.findBySanPhamId(id);
        
        return new Object() {
            public SanPham getProduct() { return sp; }
            public List<SanPhamChiTiet> getDetails() { return details; }
        };
    }

    @Transactional
    public SanPham saveProductWithVariants(SanPham sanPham, List<SanPhamChiTiet> variants) {
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

        return savedProduct;
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

    // --- User (Staff & Customer) ---
    public List<NguoiDung> getAllUsers() {
        return nguoiDungRepository.findAll();
    }

    public List<NguoiDung> getUsersByRole(Integer roleId) {
        return nguoiDungRepository.findByVaiTroId(roleId);
    }

    // --- Bill ---
    public List<HoaDon> getAllBills() {
        return hoaDonRepository.findAll();
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

        return new Object() {
            public HoaDon getBill() { return hoaDon; }
            public List<HoaDonChiTiet> getItems() { return items; }
            public List<LichSuHoaDon> getHistory() { return history; }
        };
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
        
        // Sử dụng logic nativeAddToCart hoặc thủ công để tránh trùng lặp trong giỏ
        String ma = "GHCT-POS-" + System.currentTimeMillis();
        gioHangChiTietRepository.nativeAddToCart(id, spctId, quantity, spct.getGiaBan(), ma);
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

    @Transactional
    public void applyVoucher(Integer id, String voucherCode) {
        // Đối với POS dùng GioHang, việc áp dụng voucher có thể xử lý lúc checkout 
        // hoặc lưu tạm vào một trường trong GioHang (nếu có). 
        // Hiện tại để tránh lỗi biên dịch, ta có thể tìm voucher để validate trước.
        maGiamGiaRepository.findByMaCode(voucherCode)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
    }

    @Transactional
    public void checkoutPOS(Integer id, Integer paymentMethodId, String note, Integer customerId) {
        // id là id_gio_hang
        GioHang gh = gioHangRepository.findById(id).orElseThrow();
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(id);
        if (items.isEmpty()) throw new RuntimeException("Giỏ hàng trống!");

        PtThanhToan pttt = ptThanhToanRepository.findById(paymentMethodId).orElseThrow();
        NguoiDung customer = (customerId != null) ? nguoiDungRepository.findById(customerId).orElseThrow() : null;

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

    // --- Attributes ---
    public List<PtThanhToan> getAllPaymentMethods() { return ptThanhToanRepository.findAll(); }
    public List<MauSac> getAllColors() { return mauSacRepository.findAll(); }
    public List<KichThuoc> getAllSizes() { return kichThuocRepository.findAll(); }
    public List<ChatLieu> getAllMaterials() { return chatLieuRepository.findAll(); }
}
