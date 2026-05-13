package com.example.sport_be.service;

import com.example.sport_be.dto.BenChiuLoiRequest;
import com.example.sport_be.dto.DoiTraChiTietRequest;
import com.example.sport_be.dto.DoiTraRequest;
import com.example.sport_be.dto.DuyetKiemRequest;
import com.example.sport_be.dto.HoanTienRequest;
import com.example.sport_be.dto.KhachXacNhanThuCongRequest;
import com.example.sport_be.dto.KiemHangRequest;
import com.example.sport_be.dto.RefundConfirmInfoResponse;
import com.example.sport_be.dto.XacNhanNhanTienRequest;
import com.example.sport_be.entity.CauHinhDoiTra;
import com.example.sport_be.entity.DoiTra;
import com.example.sport_be.entity.DoiTraChiTiet;
import com.example.sport_be.entity.HangLoi;
import com.example.sport_be.entity.HoaDon;
import com.example.sport_be.entity.HoaDonChiTiet;
import com.example.sport_be.entity.LichSuDoiTra;
import com.example.sport_be.entity.SanPhamChiTiet;
import com.example.sport_be.repository.CauHinhDoiTraRepository;
import com.example.sport_be.repository.DoiTraChiTietRepository;
import com.example.sport_be.repository.DoiTraRepository;
import com.example.sport_be.repository.HangLoiRepository;
import com.example.sport_be.repository.HoaDonChiTietRepository;
import com.example.sport_be.repository.HoaDonRepository;
import com.example.sport_be.repository.LichSuDoiTraRepository;
import com.example.sport_be.repository.SanPhamChiTietRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoiTraService {
    private static final List<String> ALLOWED_ORDER_STATUSES = List.of("DA_GIAO", "YEU_CAU_TRA_HANG", "HOAN_TRA_MOT_PHAN");
    private static final String ORDER_STATUS_DELIVERED = "DA_GIAO";
    private static final String ORDER_STATUS_PENDING_RETURN = "YEU_CAU_TRA_HANG";
    private static final String ORDER_STATUS_FULL_RETURN = "HOAN_TRA";

    private static final String RETURN_TYPE_REFUND = "HOAN_TIEN";
    private static final String RETURN_TYPE_EXCHANGE = "DOI_HANG";

    private static final String RETURN_STATUS_REJECTED = "TU_CHOI";
    private static final String RETURN_STATUS_CANCELLED = "CANCELLED";
    private static final String RETURN_STATUS_COMPLETED = "HOAN_THANH";
    private static final String RETURN_STATUS_DA_NHAN_HANG = "DA_NHAN_HANG";
    private static final String RETURN_STATUS_DA_KIEM_CHO_DUYET = "DA_KIEM_CHO_DUYET";
    private static final String RETURN_STATUS_DA_DUYET_CHO_HOAN_TIEN = "DA_DUYET_CHO_HOAN_TIEN";
    private static final String RETURN_STATUS_CHO_KHACH_XAC_NHAN = "CHO_KHACH_XAC_NHAN";

    private static final String BEN_CHIU_LOI_SHOP = "SHOP";
    private static final String BEN_CHIU_LOI_KHACH = "KHACH";

    private static final String PHUONG_THUC_CHUYEN_KHOAN = "CHUYEN_KHOAN";
    private static final String PHUONG_THUC_TIEN_MAT = "TIEN_MAT";
    private static final String PHUONG_THUC_VNPAY = "VNPAY";

    private static final String KET_QUA_DAT = "DAT";
    private static final String KET_QUA_HANG_LOI = "HANG_LOI";

    private final DoiTraRepository doiTraRepository;
    private final DoiTraChiTietRepository doiTraChiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final HangLoiRepository hangLoiRepository;
    private final LichSuDoiTraRepository lichSuDoiTraRepository;
    private final CauHinhDoiTraRepository cauHinhDoiTraRepository;
    private final VNPayService vnPayService;
    private final MailService mailService;

    @Value("${app.frontend-url:http://localhost:5174}")
    private String frontendUrl;

    private volatile CauHinhDoiTra cauHinhCache;

    @PostConstruct
    public void initCauHinh() {
        reloadCauHinh();
    }

    public void reloadCauHinh() {
        this.cauHinhCache = cauHinhDoiTraRepository.findTopByOrderByIdAsc().orElseGet(() -> {
            CauHinhDoiTra ch = new CauHinhDoiTra();
            ch.setPhiXuLyPhanTram(new BigDecimal("5.00"));
            ch.setPhiShipHoan(new BigDecimal("30000"));
            ch.setSoNgayChoPhep(7);
            return cauHinhDoiTraRepository.save(ch);
        });
    }

    public CauHinhDoiTra getCauHinh() {
        if (cauHinhCache == null) {
            reloadCauHinh();
        }
        return cauHinhCache;
    }

    private void logLichSu(DoiTra doiTra, HoaDon hoaDon, String hanhDong, String chiTiet) {
        LichSuDoiTra log = new LichSuDoiTra();
        log.setDoiTra(doiTra);
        log.setHoaDon(hoaDon);
        log.setHanhDong(hanhDong);
        log.setChiTiet(chiTiet);
        lichSuDoiTraRepository.save(log);
    }

    @Getter
    public static class KetQuaTinhHoan {
        private BigDecimal tienHangHoan = BigDecimal.ZERO;
        private BigDecimal phiXuLy = BigDecimal.ZERO;
        private BigDecimal phiShipHoanTru = BigDecimal.ZERO;
        private BigDecimal tongTienHoan = BigDecimal.ZERO;
    }

    private KetQuaTinhHoan tinhTienHoanItem(HoaDon hoaDon,
                                            HoaDonChiTiet hdct,
                                            int soLuongTra,
                                            String benChiuLoi,
                                            CauHinhDoiTra cauHinh) {
        KetQuaTinhHoan kq = new KetQuaTinhHoan();

        BigDecimal tienHangGoc = safe(hdct.getDonGia()).multiply(BigDecimal.valueOf(soLuongTra));
        BigDecimal tongTienHang = safe(hoaDon.getTongTienHang());
        BigDecimal tienGiamTong = safe(hoaDon.getTienGiam());

        BigDecimal tienGiamPhanBo = BigDecimal.ZERO;
        if (tongTienHang.compareTo(BigDecimal.ZERO) > 0) {
            tienGiamPhanBo = tienHangGoc.multiply(tienGiamTong)
                    .divide(tongTienHang, 0, RoundingMode.CEILING);
        }

        kq.tienHangHoan = tienHangGoc.subtract(tienGiamPhanBo).max(BigDecimal.ZERO);

        String ben = normalizeBenChiuLoi(benChiuLoi);
        if (BEN_CHIU_LOI_KHACH.equals(ben)) {
            BigDecimal pt = safe(cauHinh.getPhiXuLyPhanTram());
            kq.phiXuLy = kq.tienHangHoan.multiply(pt)
                    .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP)
                    .max(BigDecimal.ZERO);
            kq.phiShipHoanTru = safe(cauHinh.getPhiShipHoan()).max(BigDecimal.ZERO);
        }

        kq.tongTienHoan = kq.tienHangHoan
                .subtract(kq.phiXuLy)
                .subtract(kq.phiShipHoanTru)
                .max(BigDecimal.ZERO);

        return kq;
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String normalizeReturnType(String loaiDoiTra) {
        String normalized = normalizeValue(loaiDoiTra);
        if ("EXCHANGE".equals(normalized) || RETURN_TYPE_EXCHANGE.equals(normalized)) {
            return RETURN_TYPE_EXCHANGE;
        }
        return RETURN_TYPE_REFUND;
    }

    private String normalizeBenChiuLoi(String benChiuLoi) {
        String normalized = normalizeValue(benChiuLoi);
        return BEN_CHIU_LOI_SHOP.equals(normalized) ? BEN_CHIU_LOI_SHOP : BEN_CHIU_LOI_KHACH;
    }

    private String normalizePhuongThucHoan(String value) {
        String normalized = normalizeValue(value);
        if (PHUONG_THUC_CHUYEN_KHOAN.equals(normalized)) return PHUONG_THUC_CHUYEN_KHOAN;
        if (PHUONG_THUC_TIEN_MAT.equals(normalized)) return PHUONG_THUC_TIEN_MAT;
        if (PHUONG_THUC_VNPAY.equals(normalized)) return PHUONG_THUC_VNPAY;
        throw new RuntimeException("Phuong thuc hoan khong hop le");
    }

    private String normalizeKiemResult(String value) {
        String normalized = normalizeValue(value);
        if (List.of("CHUA_KIEM", "DAT", "KHONG_DAT", "HANG_LOI").contains(normalized)) {
            return normalized;
        }
        throw new RuntimeException("Ket qua kiem khong hop le");
    }

    private String normalizeValue(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeFreeText(String text) {
        return text == null ? null : text.trim();
    }

    private void require(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }
    }

    private String toJsonArray(List<String> arr) {
        if (arr == null) return null;
        return new JSONArray(arr).toString();
    }

    private String toJsonObject(Map<String, Boolean> map) {
        if (map == null) return null;
        return new JSONObject(map).toString();
    }

    private void ensureStatus(DoiTra doiTra, String... allowed) {
        String st = normalizeValue(doiTra.getTrangThai());
        for (String a : allowed) {
            if (st.equals(normalizeValue(a))) {
                return;
            }
        }
        throw new RuntimeException("Trang thai khong hop le cho hanh dong nay");
    }

    private boolean equalsIgnoreCaseTrim(String a, String b) {
        return normalizeValue(a).equals(normalizeValue(b));
    }

    private void validateExchangePriceNotHigher(HoaDonChiTiet hdct, Integer idSpctMoi) {
        if (idSpctMoi == null) {
            throw new RuntimeException("Chua chon san pham doi moi");
        }
        SanPhamChiTiet spctMoi = sanPhamChiTietRepository.findById(idSpctMoi)
                .orElseThrow(() -> new RuntimeException("San pham doi khong ton tai"));
        BigDecimal giaMoi = safe(spctMoi.getGiaBan());
        BigDecimal giaCu = safe(hdct.getDonGia());
        if (giaMoi.compareTo(giaCu) > 0) {
            String tenMoi = spctMoi.getSanPham() != null ? spctMoi.getSanPham().getTenSanPham() : "San pham moi";
            throw new RuntimeException(
                    "Sản phẩm đổi (" + tenMoi + ") có giá " + giaMoi +
                            " cao hơn sản phẩm gốc (" + giaCu +
                            "). Vui lòng huỷ yêu cầu đổi và đặt đơn mới."
            );
        }
    }

    private void recomputeRefund(DoiTra doiTra) {
        CauHinhDoiTra cauHinh = getCauHinh();
        List<DoiTraChiTiet> chiTiets = doiTraChiTietRepository.findByDoiTraId(doiTra.getId());
        doiTra.setChiTiets(chiTiets);

        BigDecimal tongTienHangHoan = BigDecimal.ZERO;
        BigDecimal tongPhiXuLy = BigDecimal.ZERO;
        BigDecimal tongPhiShipHoanTru = BigDecimal.ZERO;
        BigDecimal tongTienHoan = BigDecimal.ZERO;

        for (DoiTraChiTiet dtct : chiTiets) {
            KetQuaTinhHoan kq = tinhTienHoanItem(
                    doiTra.getHoaDon(),
                    dtct.getHoaDonChiTiet(),
                    dtct.getSoLuongTra(),
                    doiTra.getBenChiuLoi(),
                    cauHinh
            );
            dtct.setGiaTriHoan(kq.getTongTienHoan());
            tongTienHangHoan = tongTienHangHoan.add(kq.getTienHangHoan());
            tongPhiXuLy = tongPhiXuLy.add(kq.getPhiXuLy());
            tongPhiShipHoanTru = tongPhiShipHoanTru.add(kq.getPhiShipHoanTru());
            tongTienHoan = tongTienHoan.add(kq.getTongTienHoan());
        }

        doiTraChiTietRepository.saveAll(chiTiets);
        doiTra.setTienHangHoan(tongTienHangHoan);
        doiTra.setPhiXuLy(tongPhiXuLy);
        doiTra.setPhiShipHoanTru(tongPhiShipHoanTru);
        doiTra.setTongTienHoan(tongTienHoan.max(BigDecimal.ZERO));
    }

    @Transactional
    public DoiTra taoYeuCauDoiTra(DoiTraRequest request, MultipartFile[] files, String baseUrl) {
        HoaDon hoaDon = hoaDonRepository.findById(request.getHoaDonId())
                .orElseThrow(() -> new RuntimeException("Hoa don khong ton tai"));

        if (!ALLOWED_ORDER_STATUSES.contains(normalizeValue(hoaDon.getTrangThaiDon()))) {
            throw new RuntimeException("Trang thai don hang khong ho tro doi tra");
        }

        int soNgayChoPhep = getCauHinh().getSoNgayChoPhep() == null ? 7 : getCauHinh().getSoNgayChoPhep();
        LocalDateTime ngayMoc = hoaDon.getNgayNhanHang() != null
                ? hoaDon.getNgayNhanHang()
                : (hoaDon.getNgayCapNhat() != null ? hoaDon.getNgayCapNhat() : hoaDon.getNgayTao());
        if (ngayMoc != null && LocalDateTime.now().isAfter(ngayMoc.plusDays(soNgayChoPhep))) {
            throw new RuntimeException("Da qua thoi han " + soNgayChoPhep + " ngay de yeu cau doi tra");
        }

        String normalizedLoaiDoiTra = normalizeReturnType(request.getLoaiDoiTra());
        if ("TAI_QUAY".equalsIgnoreCase(hoaDon.getLoaiDonHang()) && RETURN_TYPE_REFUND.equals(normalizedLoaiDoiTra)) {
            throw new RuntimeException("Don hang tai quay khong duoc phep hoan tien");
        }

        List<DoiTra> existingRequests = doiTraRepository.findByHoaDonId(hoaDon.getId());

        for (DoiTraChiTietRequest ctReq : request.getChiTiets()) {
            HoaDonChiTiet hdct = hoaDonChiTietRepository.findById(ctReq.getHoaDonChiTietId())
                    .orElseThrow(() -> new RuntimeException("San pham khong ton tai"));

            int tongDaTra = existingRequests.stream()
                    .filter(r -> !RETURN_STATUS_REJECTED.equals(r.getTrangThai()) && !RETURN_STATUS_CANCELLED.equals(r.getTrangThai()))
                    .flatMap(r -> {
                        List<DoiTraChiTiet> list = r.getChiTiets();
                        if (list == null || list.isEmpty()) {
                            list = doiTraChiTietRepository.findByDoiTraId(r.getId());
                        }
                        return list.stream();
                    })
                    .filter(dtct -> dtct.getHoaDonChiTiet().getId().equals(hdct.getId()))
                    .mapToInt(DoiTraChiTiet::getSoLuongTra)
                    .sum();

            int conLai = hdct.getSoLuong() - tongDaTra;
            if (ctReq.getSoLuongTra() <= 0 || ctReq.getSoLuongTra() > conLai) {
                throw new RuntimeException(
                        "So luong tra (" + ctReq.getSoLuongTra() + ") vuot qua so luong kha dung (" + conLai + ") cua " + hdct.getTenSanPham()
                );
            }

            if (RETURN_TYPE_EXCHANGE.equals(normalizedLoaiDoiTra)) {
                validateExchangePriceNotHigher(hdct, ctReq.getIdSpctMoi());
            }
        }

        DoiTra doiTra = new DoiTra();
        doiTra.setMaDoiTra("DT" + System.currentTimeMillis());
        doiTra.setHoaDon(hoaDon);
        doiTra.setLyDo(request.getLyDo());
        doiTra.setLoaiDoiTra(normalizedLoaiDoiTra);
        doiTra.setTrangThai("CHO_XAC_NHAN");
        doiTra.setNgayYeuCau(LocalDateTime.now());
        doiTra.setTinhTrangHang("NGUYEN_VEN");
        doiTra.setBenChiuLoi(BEN_CHIU_LOI_KHACH);
        doiTra = doiTraRepository.save(doiTra);

        if (files != null && files.length > 0) {
            String[] savedPaths = com.example.sport_be.config.FileStorageUtils.saveFiles(files, doiTra.getId(), baseUrl);
            doiTra.setDanhSachAnh("[" + String.join(",", Arrays.stream(savedPaths).map(p -> "\"" + p + "\"").toArray(String[]::new)) + "]");
        }

        for (DoiTraChiTietRequest ctReq : request.getChiTiets()) {
            HoaDonChiTiet hdct = hoaDonChiTietRepository.findById(ctReq.getHoaDonChiTietId())
                    .orElseThrow(() -> new RuntimeException("Hoa don chi tiet khong ton tai"));

            KetQuaTinhHoan kq = tinhTienHoanItem(
                    hoaDon,
                    hdct,
                    ctReq.getSoLuongTra(),
                    BEN_CHIU_LOI_KHACH,
                    getCauHinh()
            );

            if (RETURN_TYPE_EXCHANGE.equals(normalizedLoaiDoiTra)) {
                SanPhamChiTiet spctMoi = sanPhamChiTietRepository.findById(ctReq.getIdSpctMoi())
                        .orElseThrow(() -> new RuntimeException("San pham doi khong ton tai"));
                require(spctMoi.getSoLuong() != null && spctMoi.getSoLuong() >= ctReq.getSoLuongTra(), "San pham doi khong du ton kho");
                doiTra.setIdSpctMoi(ctReq.getIdSpctMoi());
                spctMoi.setSoLuong(spctMoi.getSoLuong() - ctReq.getSoLuongTra());
                sanPhamChiTietRepository.save(spctMoi);
            }

            doiTraChiTietRepository.insertDoiTraChiTiet(
                    doiTra.getId(),
                    hdct.getId(),
                    ctReq.getSoLuongTra(),
                    kq.getTongTienHoan()
            );
        }

        List<DoiTraChiTiet> listDtct = doiTraChiTietRepository.findByDoiTraId(doiTra.getId());
        doiTra.setChiTiets(listDtct);

        recomputeRefund(doiTra);

        logLichSu(doiTra, hoaDon, "TAO_YEU_CAU", "Tao yeu cau " + normalizedLoaiDoiTra + " (uoc tinh ben chiu loi: KHACH)");

        hoaDon.setTrangThaiDon(ORDER_STATUS_PENDING_RETURN);
        hoaDonRepository.save(hoaDon);

        DoiTra saved = doiTraRepository.save(doiTra);
        saved.setChiTiets(doiTraChiTietRepository.findByDoiTraId(saved.getId()));
        return saved;
    }

    public List<DoiTra> getAllDoiTra() {
        return doiTraRepository.findAll();
    }

    public DoiTra getDoiTraById(Integer id) {
        DoiTra doiTra = doiTraRepository.findById(id).orElseThrow(() -> new RuntimeException("Khong tim thay"));
        if (doiTra.getChiTiets() == null || doiTra.getChiTiets().isEmpty()) {
            doiTra.setChiTiets(doiTraChiTietRepository.findByDoiTraId(id));
        }
        return doiTra;
    }

    public List<DoiTra> getDoiTraByHoaDon(Integer hoaDonId) {
        return doiTraRepository.findByHoaDonId(hoaDonId);
    }

    public List<LichSuDoiTra> getLogsByHoaDon(Integer hoaDonId) {
        return lichSuDoiTraRepository.findByHoaDonIdOrderByNgayTaoDesc(hoaDonId);
    }

    public List<LichSuDoiTra> getLogsByDoiTra(Integer doiTraId) {
        return lichSuDoiTraRepository.findByDoiTraIdOrderByNgayTaoDesc(doiTraId);
    }

    @Transactional
    public DoiTra xacNhan(Integer id) {
        DoiTra doiTra = getDoiTraById(id);
        ensureStatus(doiTra, "CHO_XAC_NHAN");
        doiTra.setTrangThai("CHO_TRA_HANG");

        logLichSu(doiTra, doiTra.getHoaDon(), "XAC_NHAN", "Cho khach giao hang");
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra tuChoi(Integer id) {
        DoiTra doiTra = getDoiTraById(id);
        ensureStatus(doiTra,
                "CHO_XAC_NHAN",
                "CHO_TRA_HANG",
                RETURN_STATUS_DA_NHAN_HANG,
                RETURN_STATUS_DA_KIEM_CHO_DUYET,
                RETURN_STATUS_DA_DUYET_CHO_HOAN_TIEN);
        doiTra.setTrangThai(RETURN_STATUS_REJECTED);
        if (RETURN_TYPE_EXCHANGE.equals(doiTra.getLoaiDoiTra())) {
            for (DoiTraChiTiet dtct : doiTra.getChiTiets()) {
                if (doiTra.getIdSpctMoi() != null) {
                    SanPhamChiTiet spctMoi = sanPhamChiTietRepository.findById(doiTra.getIdSpctMoi()).orElse(null);
                    if (spctMoi != null) {
                        spctMoi.setSoLuong((spctMoi.getSoLuong() == null ? 0 : spctMoi.getSoLuong()) + dtct.getSoLuongTra());
                        sanPhamChiTietRepository.save(spctMoi);
                    }
                }
            }
        }

        HoaDon hd = doiTra.getHoaDon();
        List<DoiTra> otherRequests = doiTraRepository.findByHoaDonId(hd.getId());
        boolean stillProcessing = otherRequests.stream()
                .anyMatch(r -> !r.getId().equals(id)
                        && !RETURN_STATUS_COMPLETED.equals(r.getTrangThai())
                        && !RETURN_STATUS_REJECTED.equals(r.getTrangThai())
                        && !RETURN_STATUS_CANCELLED.equals(r.getTrangThai()));

        if (!stillProcessing) {
            checkAndUpdateHoaDonFullRefund(hd.getId());
        }

        logLichSu(doiTra, doiTra.getHoaDon(), "TU_CHOI", "Admin tu choi");
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra daNhanHang(Integer id) {
        DoiTra doiTra = getDoiTraById(id);
        ensureStatus(doiTra, "CHO_TRA_HANG");
        doiTra.setTrangThai(RETURN_STATUS_DA_NHAN_HANG);
        logLichSu(doiTra, doiTra.getHoaDon(), "DA_NHAN_HANG", "Da nhan hang, bat dau kiem tra");
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra updateBenChiuLoi(Integer id, BenChiuLoiRequest request) {
        DoiTra doiTra = getDoiTraById(id);
        ensureStatus(doiTra, RETURN_STATUS_DA_NHAN_HANG, RETURN_STATUS_DA_KIEM_CHO_DUYET, RETURN_STATUS_DA_DUYET_CHO_HOAN_TIEN);

        String ben = normalizeBenChiuLoi(request.getBenChiuLoi());
        doiTra.setBenChiuLoi(ben);
        recomputeRefund(doiTra);

        logLichSu(doiTra, doiTra.getHoaDon(), "CAP_NHAT_BEN_CHIU_LOI", "Ben chiu loi = " + ben);
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra kiemHang(Integer id, KiemHangRequest request) {
        DoiTra doiTra = getDoiTraById(id);
        ensureStatus(doiTra, RETURN_STATUS_DA_NHAN_HANG);

        require(request != null, "Thieu du lieu kiem hang");
        require(request.getNguoiKiem() != null && !request.getNguoiKiem().isBlank(), "Thieu nguoi kiem");
        require(request.getChiTietKiem() != null && !request.getChiTietKiem().isEmpty(), "Thieu chi tiet kiem");

        Map<Integer, DoiTraChiTiet> mapDtct = doiTra.getChiTiets().stream()
                .collect(java.util.stream.Collectors.toMap(DoiTraChiTiet::getId, v -> v));

        for (KiemHangRequest.ChiTietKiemItem item : request.getChiTietKiem()) {
            DoiTraChiTiet dtct = mapDtct.get(item.getIdDtct());
            require(dtct != null, "Khong tim thay chi tiet doi tra id=" + item.getIdDtct());

            String skuNhap = normalizeFreeText(item.getSkuDoiChieu());
            String skuGoc = dtct.getHoaDonChiTiet().getSanPhamChiTiet() != null
                    ? dtct.getHoaDonChiTiet().getSanPhamChiTiet().getMa()
                    : null;
            require(skuNhap != null && !skuNhap.isBlank(), "Thieu SKU doi chieu");
            require(equalsIgnoreCaseTrim(skuNhap, skuGoc), "Hang khong khop don");

            require(item.getAnhKiem() != null && !item.getAnhKiem().isEmpty(), "Moi dong phai co it nhat 1 anh kiem");

            dtct.setSkuDoiChieu(skuNhap);
            dtct.setKetQuaKiem(normalizeKiemResult(item.getKetQuaKiem()));
            dtct.setChecklistJson(toJsonObject(item.getChecklist()));
            dtct.setAnhKiem(toJsonArray(item.getAnhKiem()));
            dtct.setNguoiKiem(request.getNguoiKiem().trim());
            dtct.setThoiGianKiem(LocalDateTime.now());
            dtct.setGhiChuKiem(normalizeFreeText(item.getGhiChuKiem()));
        }

        doiTraChiTietRepository.saveAll(doiTra.getChiTiets());
        doiTra.setTrangThai(RETURN_STATUS_DA_KIEM_CHO_DUYET);
        logLichSu(doiTra, doiTra.getHoaDon(), "KIEM_HANG", "Kiem hang boi " + request.getNguoiKiem());
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra duyetKiem(Integer id, DuyetKiemRequest request) {
        DoiTra doiTra = getDoiTraById(id);
        ensureStatus(doiTra, RETURN_STATUS_DA_KIEM_CHO_DUYET);

        require(request != null, "Thieu du lieu duyet kiem");
        require(request.getNguoiDuyet() != null && !request.getNguoiDuyet().isBlank(), "Thieu nguoi duyet");

        String nguoiDuyet = request.getNguoiDuyet().trim();

        for (DoiTraChiTiet dtct : doiTra.getChiTiets()) {
            require(dtct.getNguoiKiem() != null && !dtct.getNguoiKiem().isBlank(), "Chua co nguoi kiem");
            require(!equalsIgnoreCaseTrim(nguoiDuyet, dtct.getNguoiKiem()), "Nguoi duyet khong duoc trung nguoi kiem");
            dtct.setNguoiDuyet(nguoiDuyet);
            dtct.setThoiGianDuyet(LocalDateTime.now());
        }

        doiTraChiTietRepository.saveAll(doiTra.getChiTiets());
        doiTra.setTrangThai(RETURN_STATUS_DA_DUYET_CHO_HOAN_TIEN);
        logLichSu(doiTra, doiTra.getHoaDon(), "DUYET_KIEM", "Duyet boi " + nguoiDuyet + "; ghi chu: " + normalizeFreeText(request.getGhiChuDuyet()));
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra duyetTraLai(Integer id, String ghiChu) {
        DoiTra doiTra = getDoiTraById(id);
        ensureStatus(doiTra, RETURN_STATUS_DA_KIEM_CHO_DUYET);

        for (DoiTraChiTiet dtct : doiTra.getChiTiets()) {
            dtct.setSkuDoiChieu(null);
            dtct.setKetQuaKiem(null);
            dtct.setChecklistJson(null);
            dtct.setAnhKiem(null);
            dtct.setNguoiKiem(null);
            dtct.setThoiGianKiem(null);
            dtct.setNguoiDuyet(null);
            dtct.setThoiGianDuyet(null);
            dtct.setGhiChuKiem(null);
        }

        doiTraChiTietRepository.saveAll(doiTra.getChiTiets());
        doiTra.setTrangThai(RETURN_STATUS_DA_NHAN_HANG);
        logLichSu(doiTra, doiTra.getHoaDon(), "DUYET_TRA_LAI", "Tra lai kiem hang. Ghi chu: " + normalizeFreeText(ghiChu));
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra quyetDinh(Integer id, String action, Map<Integer, Boolean> chiTietKiemKho, String ghiChuAdmin) {
        DoiTra doiTra = getDoiTraById(id);

        if ("TU_CHOI".equalsIgnoreCase(action)) {
            return tuChoi(id);
        }

        ensureStatus(doiTra, RETURN_STATUS_DA_DUYET_CHO_HOAN_TIEN);
        require(doiTra.getNgayXuLy() == null, "Yeu cau da duoc xu ly kho truoc do");

        for (DoiTraChiTiet dtct : doiTra.getChiTiets()) {
            SanPhamChiTiet spctGoc = dtct.getHoaDonChiTiet().getSanPhamChiTiet();

            boolean isLoi;
            if (chiTietKiemKho != null && chiTietKiemKho.containsKey(dtct.getId())) {
                isLoi = Boolean.TRUE.equals(chiTietKiemKho.get(dtct.getId()));
            } else {
                isLoi = KET_QUA_HANG_LOI.equals(normalizeValue(dtct.getKetQuaKiem()))
                        || "KHONG_DAT".equals(normalizeValue(dtct.getKetQuaKiem()));
            }

            if (isLoi) {
                HangLoi hl = new HangLoi();
                hl.setSanPhamChiTiet(spctGoc);
                hl.setHoaDon(doiTra.getHoaDon());
                hl.setSoLuong(dtct.getSoLuongTra());
                hl.setLyDo((dtct.getGhiChuKiem() == null ? "" : dtct.getGhiChuKiem())
                        + (ghiChuAdmin == null ? "" : (" | " + ghiChuAdmin)));
                hangLoiRepository.save(hl);
            } else {
                int soLuongMoi = (spctGoc.getSoLuong() == null ? 0 : spctGoc.getSoLuong()) + dtct.getSoLuongTra();
                int daBanCu = spctGoc.getSoLuongDaBan() == null ? 0 : spctGoc.getSoLuongDaBan();
                spctGoc.setSoLuong(soLuongMoi);
                spctGoc.setSoLuongDaBan(Math.max(daBanCu - dtct.getSoLuongTra(), 0));
                sanPhamChiTietRepository.save(spctGoc);
            }

            if (RETURN_TYPE_EXCHANGE.equals(doiTra.getLoaiDoiTra()) && doiTra.getIdSpctMoi() != null) {
                SanPhamChiTiet sm = sanPhamChiTietRepository.findById(doiTra.getIdSpctMoi()).orElse(null);
                if (sm != null) {
                    sm.setSoLuongDaBan((sm.getSoLuongDaBan() != null ? sm.getSoLuongDaBan() : 0) + dtct.getSoLuongTra());
                    sanPhamChiTietRepository.save(sm);
                }
            }
        }

        doiTra.setNgayXuLy(LocalDateTime.now());
        doiTra.setGhiChuAdmin(ghiChuAdmin);
        doiTra.setTrangThai("CHO_HOAN_TIEN");
        logLichSu(doiTra, doiTra.getHoaDon(), "QUYET_DINH", "Da xu ly nhap kho/hang loi");
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra hoanTien(Integer id, HoanTienRequest request) {
        DoiTra doiTra = getDoiTraById(id);
        ensureStatus(doiTra, "CHO_HOAN_TIEN");

        require(request != null, "Thieu du lieu hoan tien");
        String phuongThuc = normalizePhuongThucHoan(request.getPhuongThucHoan());

        if (PHUONG_THUC_CHUYEN_KHOAN.equals(phuongThuc)) {
            require(request.getSoTkNhan() != null && !request.getSoTkNhan().isBlank(), "Thieu so tai khoan nhan");
            require(request.getNganHang() != null && !request.getNganHang().isBlank(), "Thieu ngan hang");
            require(request.getMaGiaoDichHoan() != null && !request.getMaGiaoDichHoan().isBlank(), "Thieu ma giao dich hoan");
            require(request.getAnhChungTu() != null && !request.getAnhChungTu().isEmpty(), "Can it nhat 1 anh bien lai");
        }

        if (PHUONG_THUC_TIEN_MAT.equals(phuongThuc)) {
            require("TAI_QUAY".equalsIgnoreCase(doiTra.getHoaDon().getLoaiDonHang()), "Tien mat chi ap dung don TAI_QUAY");
            require(request.getAnhChungTu() != null && !request.getAnhChungTu().isEmpty(), "Can it nhat 1 anh ky xac nhan");
        }

        doiTra.setPhuongThucHoan(phuongThuc);
        doiTra.setSoTkNhan(normalizeFreeText(request.getSoTkNhan()));
        doiTra.setTenChuTk(normalizeFreeText(request.getTenChuTk()));
        doiTra.setNganHang(normalizeFreeText(request.getNganHang()));
        doiTra.setMaGiaoDichHoan(normalizeFreeText(request.getMaGiaoDichHoan()));
        doiTra.setAnhChungTu(toJsonArray(request.getAnhChungTu()));
        doiTra.setTokenXacNhan(UUID.randomUUID().toString().replace("-", ""));
        doiTra.setTrangThai(RETURN_STATUS_CHO_KHACH_XAC_NHAN);

        DoiTra saved = doiTraRepository.save(doiTra);

        if (PHUONG_THUC_VNPAY.equals(phuongThuc)) {
            logLichSu(saved, saved.getHoaDon(), "HOAN_TIEN", "VNPAY out-of-scope phase nay. Da luu thong tin");
        } else {
            logLichSu(saved, saved.getHoaDon(), "HOAN_TIEN", "Da thuc hien hoan tien qua " + phuongThuc);
        }

        boolean sent = mailService.sendRefundConfirmation(saved);
        if (!sent) {
            logLichSu(saved, saved.getHoaDon(), "EMAIL_XAC_NHAN", "SMTP chua cau hinh/khong kha dung, bo qua gui mail");
        }
        return saved;
    }

    public RefundConfirmInfoResponse getConfirmInfo(String token) {
        require(token != null && !token.isBlank(), "Token khong hop le");
        DoiTra doiTra = doiTraRepository.findByTokenXacNhan(token.trim())
                .orElseThrow(() -> new RuntimeException("Token khong hop le"));

        RefundConfirmInfoResponse res = new RefundConfirmInfoResponse();
        res.setMaDoiTra(doiTra.getMaDoiTra());
        res.setTongTienHoan(doiTra.getTongTienHoan());
        res.setTienHangHoan(doiTra.getTienHangHoan());
        res.setPhiXuLy(doiTra.getPhiXuLy());
        res.setPhiShipHoanTru(doiTra.getPhiShipHoanTru());
        res.setPhuongThucHoan(doiTra.getPhuongThucHoan());
        res.setMaGiaoDichHoan(doiTra.getMaGiaoDichHoan());
        res.setAnhChungTu(doiTra.getAnhChungTu());
        res.setTrangThai(doiTra.getTrangThai());
        return res;
    }

    @Transactional
    public DoiTra xacNhanNhanTien(XacNhanNhanTienRequest request) {
        require(request != null && request.getToken() != null && !request.getToken().isBlank(), "Token khong hop le");
        DoiTra doiTra = doiTraRepository.findByTokenXacNhan(request.getToken().trim())
                .orElseThrow(() -> new RuntimeException("Token khong hop le"));

        if (Boolean.TRUE.equals(doiTra.getKhachXacNhanNhanTien()) || RETURN_STATUS_COMPLETED.equals(doiTra.getTrangThai())) {
            return doiTra;
        }

        require(RETURN_STATUS_CHO_KHACH_XAC_NHAN.equals(normalizeValue(doiTra.getTrangThai())), "Trang thai khong hop le");
        doiTra.setKhachXacNhanNhanTien(true);
        doiTra.setNgayKhachXacNhan(LocalDateTime.now());
        doiTra.setTrangThai(RETURN_STATUS_COMPLETED);

        DoiTra saved = doiTraRepository.save(doiTra);
        checkAndUpdateHoaDonFullRefund(saved.getHoaDon().getId());
        logLichSu(saved, saved.getHoaDon(), "KHACH_XAC_NHAN", "Khach xac nhan da nhan tien qua token");
        return saved;
    }

    @Transactional
    public DoiTra khachXacNhanThuCong(Integer id, KhachXacNhanThuCongRequest request) {
        DoiTra doiTra = getDoiTraById(id);
        require(request != null && request.getGhiChu() != null && !request.getGhiChu().isBlank(), "Bat buoc ghi chu");
        ensureStatus(doiTra, RETURN_STATUS_CHO_KHACH_XAC_NHAN, RETURN_STATUS_COMPLETED);

        if (RETURN_STATUS_COMPLETED.equals(doiTra.getTrangThai()) && Boolean.TRUE.equals(doiTra.getKhachXacNhanNhanTien())) {
            return doiTra;
        }

        doiTra.setKhachXacNhanNhanTien(true);
        doiTra.setNgayKhachXacNhan(LocalDateTime.now());
        doiTra.setTrangThai(RETURN_STATUS_COMPLETED);

        DoiTra saved = doiTraRepository.save(doiTra);
        checkAndUpdateHoaDonFullRefund(saved.getHoaDon().getId());
        logLichSu(saved, saved.getHoaDon(), "KHACH_XAC_NHAN_BOI_ADMIN", request.getGhiChu());
        return saved;
    }

    @Transactional
    public DoiTra huyYeuCau(Integer id, String ghiChu) {
        DoiTra doiTra = getDoiTraById(id);
        if (RETURN_STATUS_COMPLETED.equals(doiTra.getTrangThai())
                || RETURN_STATUS_REJECTED.equals(doiTra.getTrangThai())
                || RETURN_STATUS_CHO_KHACH_XAC_NHAN.equals(doiTra.getTrangThai())) {
            throw new RuntimeException("Da ket thuc");
        }

        if (RETURN_TYPE_EXCHANGE.equals(doiTra.getLoaiDoiTra())) {
            for (DoiTraChiTiet dtct : doiTra.getChiTiets()) {
                if (doiTra.getIdSpctMoi() != null) {
                    SanPhamChiTiet sm = sanPhamChiTietRepository.findById(doiTra.getIdSpctMoi()).orElse(null);
                    if (sm != null) {
                        sm.setSoLuong((sm.getSoLuong() == null ? 0 : sm.getSoLuong()) + dtct.getSoLuongTra());
                        sanPhamChiTietRepository.save(sm);
                    }
                }
            }
        }

        doiTra.setTrangThai(RETURN_STATUS_CANCELLED);
        doiTra.setGhiChuAdmin(ghiChu);
        DoiTra saved = doiTraRepository.save(doiTra);

        HoaDon hd = doiTra.getHoaDon();
        List<DoiTra> otherRequests = doiTraRepository.findByHoaDonId(hd.getId());
        boolean stillProcessing = otherRequests.stream()
                .anyMatch(r -> !r.getId().equals(id)
                        && !RETURN_STATUS_COMPLETED.equals(r.getTrangThai())
                        && !RETURN_STATUS_REJECTED.equals(r.getTrangThai())
                        && !RETURN_STATUS_CANCELLED.equals(r.getTrangThai()));

        if (!stillProcessing) {
            checkAndUpdateHoaDonFullRefund(hd.getId());
        }

        return saved;
    }

    private void checkAndUpdateHoaDonFullRefund(Integer hoaDonId) {
        HoaDon hd = hoaDonRepository.findById(hoaDonId).orElse(null);
        if (hd == null) {
            return;
        }

        List<DoiTra> listDoiTra = doiTraRepository.findByHoaDonId(hoaDonId);
        int tongTraSuccess = 0;
        BigDecimal tongTienDaHoan = BigDecimal.ZERO;

        for (DoiTra dt : listDoiTra) {
            if (RETURN_STATUS_COMPLETED.equals(dt.getTrangThai())) {
                List<DoiTraChiTiet> dtcts = dt.getChiTiets();
                if (dtcts == null || dtcts.isEmpty()) {
                    dtcts = doiTraChiTietRepository.findByDoiTraId(dt.getId());
                }
                tongTraSuccess += dtcts.stream().mapToInt(DoiTraChiTiet::getSoLuongTra).sum();

                if (RETURN_TYPE_REFUND.equals(normalizeReturnType(dt.getLoaiDoiTra())) && dt.getTongTienHoan() != null) {
                    tongTienDaHoan = tongTienDaHoan.add(dt.getTongTienHoan());
                }
            }
        }

        if (tongTienDaHoan.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal tongThanhToanGoc = safe(hd.getTongTienHang())
                    .subtract(safe(hd.getTienGiam()))
                    .add(safe(hd.getPhiVanChuyen()));
            hd.setTongThanhToan(tongThanhToanGoc.subtract(tongTienDaHoan).max(BigDecimal.ZERO));
        }

        if (tongTraSuccess > 0) {
            hd.setTrangThaiDon(ORDER_STATUS_FULL_RETURN);
        } else {
            hd.setTrangThaiDon(ORDER_STATUS_DELIVERED);
        }
        hoaDonRepository.save(hd);
    }
}
