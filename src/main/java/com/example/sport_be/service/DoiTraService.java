package com.example.sport_be.service;

import com.example.sport_be.dto.DoiTraChiTietRequest;
import com.example.sport_be.dto.DoiTraRequest;
import com.example.sport_be.entity.DoiTra;
import com.example.sport_be.entity.DoiTraChiTiet;
import com.example.sport_be.entity.HangLoi;
import com.example.sport_be.entity.HoaDon;
import com.example.sport_be.entity.HoaDonChiTiet;
import com.example.sport_be.entity.LichSuDoiTra;
import com.example.sport_be.entity.SanPhamChiTiet;
import com.example.sport_be.repository.DoiTraChiTietRepository;
import com.example.sport_be.repository.DoiTraRepository;
import com.example.sport_be.repository.HangLoiRepository;
import com.example.sport_be.repository.HoaDonChiTietRepository;
import com.example.sport_be.repository.HoaDonRepository;
import com.example.sport_be.repository.LichSuDoiTraRepository;
import com.example.sport_be.repository.SanPhamChiTietRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DoiTraService {
    private static final List<String> ALLOWED_ORDER_STATUSES = List.of("DA_GIAO", "YEU_CAU_TRA_HANG", "HOAN_TRA_MOT_PHAN");
    private static final String ORDER_STATUS_DELIVERED = "DA_GIAO";
    private static final String ORDER_STATUS_PENDING_RETURN = "YEU_CAU_TRA_HANG";
    private static final String ORDER_STATUS_PARTIAL_RETURN = "HOAN_TRA_MOT_PHAN";
    private static final String ORDER_STATUS_FULL_RETURN = "HOAN_TRA";
    private static final String RETURN_TYPE_REFUND = "HOAN_TIEN";
    private static final String RETURN_TYPE_EXCHANGE = "DOI_HANG";
    private static final String RETURN_STATUS_REJECTED = "TU_CHOI";
    private static final String RETURN_STATUS_CANCELLED = "CANCELLED";
    private static final String RETURN_STATUS_COMPLETED = "HOAN_THANH";

    private final DoiTraRepository doiTraRepository;
    private final DoiTraChiTietRepository doiTraChiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final HangLoiRepository hangLoiRepository;
    private final LichSuDoiTraRepository lichSuDoiTraRepository;
    private final VNPayService vnPayService;

    private void logLichSu(DoiTra doiTra, HoaDon hoaDon, String hanhDong, String chiTiet) {
        LichSuDoiTra log = new LichSuDoiTra();
        log.setDoiTra(doiTra);
        log.setHoaDon(hoaDon);
        log.setHanhDong(hanhDong);
        log.setChiTiet(chiTiet);
        lichSuDoiTraRepository.save(log);
    }

    public BigDecimal tinhTienHoanItem(HoaDon hoaDon, HoaDonChiTiet hdct, int soLuongTra) {
        BigDecimal giaGocItem = hdct.getDonGia().multiply(BigDecimal.valueOf(soLuongTra));
        BigDecimal tongTienHang = hoaDon.getTongTienHang();
        if (tongTienHang == null || tongTienHang.compareTo(BigDecimal.ZERO) <= 0) {
            return giaGocItem;
        }

        BigDecimal tienGiamTong = hoaDon.getTienGiam() != null ? hoaDon.getTienGiam() : BigDecimal.ZERO;
        BigDecimal tienGiamPhanBo = giaGocItem.multiply(tienGiamTong).divide(tongTienHang, 0, RoundingMode.CEILING);
        BigDecimal tienHoanThucTe = giaGocItem.subtract(tienGiamPhanBo);
        return tienHoanThucTe.compareTo(BigDecimal.ZERO) > 0 ? tienHoanThucTe : BigDecimal.ZERO;
    }

    @Transactional
    public DoiTra taoYeuCauDoiTra(DoiTraRequest request, MultipartFile[] files, String baseUrl) {
        HoaDon hoaDon = hoaDonRepository.findById(request.getHoaDonId())
                .orElseThrow(() -> new RuntimeException("Hoa don khong ton tai"));

        if (!ALLOWED_ORDER_STATUSES.contains(normalizeValue(hoaDon.getTrangThaiDon()))) {
            throw new RuntimeException("Trang thai don hang khong ho tro doi tra");
        }

        LocalDateTime ngayMoc = hoaDon.getNgayNhanHang() != null
                ? hoaDon.getNgayNhanHang()
                : (hoaDon.getNgayCapNhat() != null ? hoaDon.getNgayCapNhat() : hoaDon.getNgayTao());
        if (ngayMoc != null && LocalDateTime.now().isAfter(ngayMoc.plusDays(7))) {
            throw new RuntimeException("Da qua thoi han 7 ngay de yeu cau doi tra");
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
                    .flatMap(r -> r.getChiTiets().stream())
                    .filter(dtct -> dtct.getHoaDonChiTiet().getId().equals(hdct.getId()))
                    .mapToInt(DoiTraChiTiet::getSoLuongTra)
                    .sum();

            int conLai = hdct.getSoLuong() - tongDaTra;
            if (ctReq.getSoLuongTra() <= 0 || ctReq.getSoLuongTra() > conLai) {
                throw new RuntimeException(
                        "So luong tra (" + ctReq.getSoLuongTra() + ") vuot qua so luong kha dung (" + conLai + ") cua " + hdct.getTenSanPham()
                );
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
        doiTra = doiTraRepository.save(doiTra);

        if (files != null && files.length > 0) {
            String[] savedPaths = com.example.sport_be.config.FileStorageUtils.saveFiles(files, doiTra.getId(), baseUrl);
            doiTra.setDanhSachAnh("[" + String.join(",", Arrays.stream(savedPaths).map(p -> "\"" + p + "\"").toArray(String[]::new)) + "]");
        }

        BigDecimal tongTienHoan = BigDecimal.ZERO;
        for (DoiTraChiTietRequest ctReq : request.getChiTiets()) {
            HoaDonChiTiet hdct = hoaDonChiTietRepository.findById(ctReq.getHoaDonChiTietId())
                    .orElseThrow(() -> new RuntimeException("Hoa don chi tiet khong ton tai"));
            BigDecimal tienHoanItem = tinhTienHoanItem(hoaDon, hdct, ctReq.getSoLuongTra());
            tongTienHoan = tongTienHoan.add(tienHoanItem);

            if (RETURN_TYPE_EXCHANGE.equals(normalizedLoaiDoiTra)) {
                SanPhamChiTiet spctMoi = sanPhamChiTietRepository.findById(ctReq.getIdSpctMoi())
                        .orElseThrow(() -> new RuntimeException("San pham doi khong ton tai"));
                doiTra.setIdSpctMoi(ctReq.getIdSpctMoi());
                spctMoi.setSoLuong(spctMoi.getSoLuong() - ctReq.getSoLuongTra());
                sanPhamChiTietRepository.save(spctMoi);
            }

            doiTraChiTietRepository.insertDoiTraChiTiet(
                    doiTra.getId(),
                    hdct.getId(),
                    ctReq.getSoLuongTra(),
                    tienHoanItem
            );
        }

        List<DoiTraChiTiet> listDtct = doiTraChiTietRepository.findByDoiTraId(doiTra.getId());
        doiTra.setChiTiets(listDtct);
        doiTra.setTongTienHoan(tongTienHoan);

        logLichSu(doiTra, hoaDon, "TAO_YEU_CAU", "Tao yeu cau " + normalizedLoaiDoiTra);

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
        return doiTraRepository.findById(id).orElseThrow(() -> new RuntimeException("Khong tim thay"));
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
    public DoiTra xacNhanThanhToan(Integer id) {
        DoiTra doiTra = getDoiTraById(id);
        logLichSu(doiTra, doiTra.getHoaDon(), "THANH_TOAN", "Xac nhan thanh toan thu cong");
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra xacNhan(Integer id) {
        DoiTra doiTra = getDoiTraById(id);
        doiTra.setTrangThai("CHO_TRA_HANG");
        
        HoaDon hd = doiTra.getHoaDon();
        hd.setTrangThaiDon(ORDER_STATUS_FULL_RETURN);
        hoaDonRepository.save(hd);

        logLichSu(doiTra, doiTra.getHoaDon(), "XAC_NHAN", "Cho khach giao hang");
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra tuChoi(Integer id) {
        DoiTra doiTra = getDoiTraById(id);
        doiTra.setTrangThai(RETURN_STATUS_REJECTED);
        if (RETURN_TYPE_EXCHANGE.equals(doiTra.getLoaiDoiTra())) {
            for (DoiTraChiTiet dtct : doiTra.getChiTiets()) {
                if (doiTra.getIdSpctMoi() != null) {
                    SanPhamChiTiet spctMoi = sanPhamChiTietRepository.findById(doiTra.getIdSpctMoi()).orElse(null);
                    if (spctMoi != null) {
                        spctMoi.setSoLuong(spctMoi.getSoLuong() + dtct.getSoLuongTra());
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
        doiTra.setTrangThai("DA_NHAN_HANG");
        logLichSu(doiTra, doiTra.getHoaDon(), "DA_NHAN_HANG", "Da nhan hang, bat dau kiem tra");
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra quyetDinh(Integer id, String action, java.util.Map<Integer, Boolean> chiTietKiemKho, String ghiChuAdmin) {
        DoiTra doiTra = getDoiTraById(id);
        if ("TU_CHOI".equals(action)) {
            return tuChoi(id);
        }

        for (DoiTraChiTiet dtct : doiTra.getChiTiets()) {
            SanPhamChiTiet spctGoc = dtct.getHoaDonChiTiet().getSanPhamChiTiet();
            boolean isLoi = chiTietKiemKho != null && Boolean.TRUE.equals(chiTietKiemKho.get(dtct.getId()));
            if (isLoi) {
                HangLoi hl = new HangLoi();
                hl.setSanPhamChiTiet(spctGoc);
                hl.setHoaDon(doiTra.getHoaDon());
                hl.setSoLuong(dtct.getSoLuongTra());
                hl.setLyDo(ghiChuAdmin);
                hangLoiRepository.save(hl);
            } else {
                spctGoc.setSoLuong(spctGoc.getSoLuong() + dtct.getSoLuongTra());
                int newDaBan = (spctGoc.getSoLuongDaBan() != null ? spctGoc.getSoLuongDaBan() : 0) - dtct.getSoLuongTra();
                spctGoc.setSoLuongDaBan(Math.max(newDaBan, 0));
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

        doiTra.setTrangThai(RETURN_STATUS_COMPLETED);
        doiTra.setGhiChuAdmin(ghiChuAdmin);
        DoiTra saved = doiTraRepository.save(doiTra);
        checkAndUpdateHoaDonFullRefund(doiTra.getHoaDon().getId());
        return saved;
    }

    @Transactional
    public DoiTra huyYeuCau(Integer id, String ghiChu) {
        DoiTra doiTra = getDoiTraById(id);
        if (RETURN_STATUS_COMPLETED.equals(doiTra.getTrangThai()) || RETURN_STATUS_REJECTED.equals(doiTra.getTrangThai())) {
            throw new RuntimeException("Da ket thuc");
        }

        if (RETURN_TYPE_EXCHANGE.equals(doiTra.getLoaiDoiTra())) {
            for (DoiTraChiTiet dtct : doiTra.getChiTiets()) {
                if (doiTra.getIdSpctMoi() != null) {
                    SanPhamChiTiet sm = sanPhamChiTietRepository.findById(doiTra.getIdSpctMoi()).orElse(null);
                    if (sm != null) {
                        sm.setSoLuong(sm.getSoLuong() + dtct.getSoLuongTra());
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

        List<HoaDonChiTiet> listHdct = hoaDonChiTietRepository.findByHoaDonId(hoaDonId);
        int tongMua = listHdct.stream().mapToInt(HoaDonChiTiet::getSoLuong).sum();

        List<DoiTra> listDoiTra = doiTraRepository.findByHoaDonId(hoaDonId);
        int tongTraSuccess = 0;
        BigDecimal tongTienDaHoan = BigDecimal.ZERO;

        for (DoiTra dt : listDoiTra) {
            if (RETURN_STATUS_COMPLETED.equals(dt.getTrangThai())) {
                tongTraSuccess += dt.getChiTiets().stream().mapToInt(DoiTraChiTiet::getSoLuongTra).sum();
                if (RETURN_TYPE_REFUND.equals(normalizeReturnType(dt.getLoaiDoiTra())) && dt.getTongTienHoan() != null) {
                    tongTienDaHoan = tongTienDaHoan.add(dt.getTongTienHoan());
                }
            }
        }

        if (tongTienDaHoan.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal tongThanhToanGoc = hd.getTongTienHang()
                    .subtract(hd.getTienGiam() != null ? hd.getTienGiam() : BigDecimal.ZERO)
                    .add(hd.getPhiVanChuyen() != null ? hd.getPhiVanChuyen() : BigDecimal.ZERO);
            hd.setTongThanhToan(tongThanhToanGoc.subtract(tongTienDaHoan).max(BigDecimal.ZERO));
        }

        if (tongTraSuccess > 0) {
            hd.setTrangThaiDon(ORDER_STATUS_FULL_RETURN);
        } else {
            hd.setTrangThaiDon(ORDER_STATUS_DELIVERED);
        }
        hoaDonRepository.save(hd);
    }

    private String normalizeReturnType(String loaiDoiTra) {
        String normalized = normalizeValue(loaiDoiTra);
        if ("EXCHANGE".equals(normalized) || RETURN_TYPE_EXCHANGE.equals(normalized)) {
            return RETURN_TYPE_EXCHANGE;
        }
        return RETURN_TYPE_REFUND;
    }

    private String normalizeValue(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }
}
