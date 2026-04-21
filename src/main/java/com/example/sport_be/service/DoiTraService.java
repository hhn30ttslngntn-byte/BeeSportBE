package com.example.sport_be.service;

import com.example.sport_be.entity.*;
import com.example.sport_be.repository.*;
import com.example.sport_be.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoiTraService {
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
        if (tongTienHang == null || tongTienHang.compareTo(BigDecimal.ZERO) <= 0) return giaGocItem;
        BigDecimal tienGiamTong = hoaDon.getTienGiam() != null ? hoaDon.getTienGiam() : BigDecimal.ZERO;
        BigDecimal tienGiamPhanBo = giaGocItem.multiply(tienGiamTong).divide(tongTienHang, 0, RoundingMode.CEILING);
        BigDecimal tienHoanThucTe = giaGocItem.subtract(tienGiamPhanBo);
        return tienHoanThucTe.compareTo(BigDecimal.ZERO) > 0 ? tienHoanThucTe : BigDecimal.ZERO;
    }

    @Transactional
    public DoiTra taoYeuCauDoiTra(DoiTraRequest request, MultipartFile[] files, String baseUrl) {
        HoaDon hoaDon = hoaDonRepository.findById(request.getHoaDonId())
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        // 1. VALIDATION TRƯỚC KHI LÀM BẤT CỨ GÌ
        List<String> allowedStatuses = java.util.Arrays.asList("DA_GIAO", "YEU_CAU_TRA_HANG", "HOAN_TRA_MOT_PHAN");
        if (!allowedStatuses.contains(hoaDon.getTrangThaiDon())) {
            throw new RuntimeException("Trạng thái đơn hàng không hỗ trợ đổi trả");
        }

        LocalDateTime ngayMoc = hoaDon.getNgayNhanHang() != null ? hoaDon.getNgayNhanHang() : (hoaDon.getNgayCapNhat() != null ? hoaDon.getNgayCapNhat() : hoaDon.getNgayTao());
        if (LocalDateTime.now().isAfter(ngayMoc.plusDays(7))) {
            throw new RuntimeException("Đã quá thời hạn 7 ngày để yêu cầu đổi trả");
        }

        if ("TAI_QUAY".equals(hoaDon.getLoaiDonHang()) && "REFUND".equals(request.getLoaiDoiTra())) {
            throw new RuntimeException("Đơn hàng tại quầy không được phép hoàn tiền");
        }

        // Lấy danh sách yêu cầu đổi trả hữu hiệu để tính toán SL khả dụng
        List<DoiTra> existingRequests = doiTraRepository.findByHoaDonId(hoaDon.getId());

        // Kiểm tra số lượng hợp lệ cho TẤT CẢ item trước
        for (DoiTraChiTietRequest ctReq : request.getChiTiets()) {
            HoaDonChiTiet hdct = hoaDonChiTietRepository.findById(ctReq.getHoaDonChiTietId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
            
            int tongDaTra = existingRequests.stream()
                    .filter(r -> !"TU_CHOI".equals(r.getTrangThai()) && !"CANCELLED".equals(r.getTrangThai()))
                    .flatMap(r -> r.getChiTiets().stream())
                    .filter(dtct -> dtct.getHoaDonChiTiet().getId().equals(hdct.getId()))
                    .mapToInt(DoiTraChiTiet::getSoLuongTra).sum();
                    
            int conLai = hdct.getSoLuong() - tongDaTra;
            if (ctReq.getSoLuongTra() <= 0 || ctReq.getSoLuongTra() > conLai) {
                throw new RuntimeException("Số lượng trả (" + ctReq.getSoLuongTra() + ") vượt quá số lượng khả dụng (" + conLai + ") của " + hdct.getTenSanPham());
            }
        }

        // 2. KHỞI TẠO VÀ LƯU
        DoiTra doiTra = new DoiTra();
        doiTra.setMaDoiTra("DT" + System.currentTimeMillis());
        doiTra.setHoaDon(hoaDon);
        doiTra.setLyDo(request.getLyDo());
        doiTra.setLoaiDoiTra(request.getLoaiDoiTra());
        doiTra.setTrangThai("CHO_XAC_NHAN_HOAN"); 
        doiTra.setTienChenhLech(BigDecimal.ZERO);
        doiTra.setNgayYeuCau(LocalDateTime.now());
        doiTra.setTinhTrangHang("NGUYEN_VEN");
        doiTra = doiTraRepository.save(doiTra);

        if (files != null && files.length > 0) {
            String[] savedPaths = com.example.sport_be.config.FileStorageUtils.saveFiles(files, doiTra.getId(), baseUrl);
            doiTra.setDanhSachAnh("[" + String.join(",", Arrays.stream(savedPaths).map(p -> "\"" + p + "\"").toArray(String[]::new)) + "]");
        }

        BigDecimal tongTienHoan = BigDecimal.ZERO;
        BigDecimal tongGiaTriHangDoi = BigDecimal.ZERO;
        List<DoiTraChiTiet> listDtct = new ArrayList<>();

        for (DoiTraChiTietRequest ctReq : request.getChiTiets()) {
            HoaDonChiTiet hdct = hoaDonChiTietRepository.findById(ctReq.getHoaDonChiTietId()).get();
            BigDecimal tienHoanItem = tinhTienHoanItem(hoaDon, hdct, ctReq.getSoLuongTra());
            tongTienHoan = tongTienHoan.add(tienHoanItem);

            DoiTraChiTiet dtct = new DoiTraChiTiet();
            dtct.setDoiTra(doiTra);
            dtct.setHoaDonChiTiet(hdct);
            dtct.setSoLuongTra(ctReq.getSoLuongTra());
            dtct.setDonGia(hdct.getDonGia());
            dtct.setGiaTriHoan(tienHoanItem);

            if ("EXCHANGE".equals(request.getLoaiDoiTra())) {
                SanPhamChiTiet spctMoi = sanPhamChiTietRepository.findById(ctReq.getIdSpctMoi())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm đổi không tồn tại"));
                dtct.setIdSpctMoi(ctReq.getIdSpctMoi());
                tongGiaTriHangDoi = tongGiaTriHangDoi.add(spctMoi.getGiaBan().multiply(BigDecimal.valueOf(ctReq.getSoLuongTra())));
                
                // GIỮ HÀNG
                spctMoi.setSoLuong(spctMoi.getSoLuong() - ctReq.getSoLuongTra());
                sanPhamChiTietRepository.save(spctMoi);
            }
            listDtct.add(doiTraChiTietRepository.save(dtct));
        }

        doiTra.setChiTiets(listDtct);
        doiTra.setTongTienHoan(tongTienHoan);
        if ("EXCHANGE".equals(request.getLoaiDoiTra())) doiTra.setTienChenhLech(tongGiaTriHangDoi.subtract(tongTienHoan));
        
        doiTra.setTrangThaiThanhToan(doiTra.getTienChenhLech() != null && doiTra.getTienChenhLech().compareTo(BigDecimal.ZERO) == 0 ? "KHONG_CAN_THANH_TOAN" : "CHUA_THANH_TOAN");

        logLichSu(doiTra, hoaDon, "TAO_YEU_CAU", "Tạo yêu cầu " + request.getLoaiDoiTra() + ". Đã giữ kho SP mới.");
        
        // Cập nhật trạng thái hóa đơn sang đang xử lý đổi trả
        hoaDon.setTrangThaiDon("YEU_CAU_TRA_HANG");
        hoaDonRepository.save(hoaDon);

        return doiTraRepository.save(doiTra);
    }

    public List<DoiTra> getAllDoiTra() { return doiTraRepository.findAll(); }
    public DoiTra getDoiTraById(Integer id) { return doiTraRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy")); }
    public List<DoiTra> getDoiTraByHoaDon(Integer hoaDonId) { return doiTraRepository.findByHoaDonId(hoaDonId); }
    public List<LichSuDoiTra> getLogsByHoaDon(Integer hoaDonId) { return lichSuDoiTraRepository.findByHoaDonIdOrderByNgayTaoDesc(hoaDonId); }
    public List<LichSuDoiTra> getLogsByDoiTra(Integer doiTraId) { return lichSuDoiTraRepository.findByDoiTraIdOrderByNgayTaoDesc(doiTraId); }

    @Transactional
    public DoiTra xacNhanThanhToan(Integer id) {
        DoiTra doiTra = getDoiTraById(id);
        doiTra.setTrangThaiThanhToan("DA_THANH_TOAN");
        logLichSu(doiTra, doiTra.getHoaDon(), "THANH_TOAN", "Xác nhận thanh toán thủ công");
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra xacNhan(Integer id) {
        DoiTra doiTra = getDoiTraById(id);
        doiTra.setTrangThai("CHO_GIAO_HANG");
        logLichSu(doiTra, doiTra.getHoaDon(), "XAC_NHAN", "Chờ khách giao hàng");
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra tuChoi(Integer id) {
        DoiTra doiTra = getDoiTraById(id);
        doiTra.setTrangThai("TU_CHOI");
        if ("EXCHANGE".equals(doiTra.getLoaiDoiTra())) {
            for (DoiTraChiTiet dtct : doiTra.getChiTiets()) {
                if (dtct.getIdSpctMoi() != null) {
                    SanPhamChiTiet spctMoi = sanPhamChiTietRepository.findById(dtct.getIdSpctMoi()).orElse(null);
                    if (spctMoi != null) {
                        spctMoi.setSoLuong(spctMoi.getSoLuong() + dtct.getSoLuongTra());
                        sanPhamChiTietRepository.save(spctMoi);
                    }
                }
            }
        }
        
        // Trả lại trạng thái DA_GIAO cho hóa đơn nếu không còn yêu cầu nào khác đang xử lý
        HoaDon hd = doiTra.getHoaDon();
        List<DoiTra> otherRequests = doiTraRepository.findByHoaDonId(hd.getId());
        boolean stillProcessing = otherRequests.stream()
                .anyMatch(r -> !r.getId().equals(id) && !"HOAN_THANH".equals(r.getTrangThai()) && !"TU_CHOI".equals(r.getTrangThai()) && !"CANCELLED".equals(r.getTrangThai()));
        
        if (!stillProcessing) {
            checkAndUpdateHoaDonFullRefund(hd.getId());
        }

        logLichSu(doiTra, doiTra.getHoaDon(), "TU_CHOI", "Admin từ chối");
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra daNhanHang(Integer id) {
        DoiTra doiTra = getDoiTraById(id);
        doiTra.setTrangThai("DA_NHAN_HANG_KIEM_TRA");
        logLichSu(doiTra, doiTra.getHoaDon(), "DA_NHAN_HANG", "Đã nhận hàng, bắt đầu kiểm tra");
        return doiTraRepository.save(doiTra);
    }

    @Transactional
    public DoiTra quyetDinh(Integer id, String action, java.util.Map<Integer, Boolean> chiTietKiemKho, String ghiChuAdmin) {
        DoiTra doiTra = getDoiTraById(id);
        if ("TU_CHOI".equals(action)) return tuChoi(id);

        for (DoiTraChiTiet dtct : doiTra.getChiTiets()) {
            SanPhamChiTiet spctGoc = dtct.getHoaDonChiTiet().getSanPhamChiTiet();
            boolean isLoi = chiTietKiemKho != null && Boolean.TRUE.equals(chiTietKiemKho.get(dtct.getId()));
            if (isLoi) {
                HangLoi hl = new HangLoi(); hl.setSanPhamChiTiet(spctGoc); hl.setHoaDon(doiTra.getHoaDon());
                hl.setSoLuong(dtct.getSoLuongTra()); hl.setLyDo(ghiChuAdmin); hangLoiRepository.save(hl);
            } else {
                spctGoc.setSoLuong(spctGoc.getSoLuong() + dtct.getSoLuongTra());
                int newDaBan = (spctGoc.getSoLuongDaBan() != null ? spctGoc.getSoLuongDaBan() : 0) - dtct.getSoLuongTra();
                spctGoc.setSoLuongDaBan(Math.max(newDaBan, 0)); sanPhamChiTietRepository.save(spctGoc);
            }
            if ("EXCHANGE".equals(doiTra.getLoaiDoiTra()) && dtct.getIdSpctMoi() != null) {
                SanPhamChiTiet sm = sanPhamChiTietRepository.findById(dtct.getIdSpctMoi()).orElse(null);
                if (sm != null) { sm.setSoLuongDaBan((sm.getSoLuongDaBan() != null ? sm.getSoLuongDaBan() : 0) + dtct.getSoLuongTra()); sanPhamChiTietRepository.save(sm); }
            }
        }

        doiTra.setTrangThai("HOAN_THANH"); doiTra.setGhiChuAdmin(ghiChuAdmin);
        DoiTra saved = doiTraRepository.save(doiTra);
        checkAndUpdateHoaDonFullRefund(doiTra.getHoaDon().getId());
        return saved;
    }

    @Transactional
    public DoiTra huyYeuCau(Integer id, String ghiChu) {
        DoiTra doiTra = getDoiTraById(id);
        if ("HOAN_THANH".equals(doiTra.getTrangThai()) || "TU_CHOI".equals(doiTra.getTrangThai())) throw new RuntimeException("Đã kết thúc");
        if ("EXCHANGE".equals(doiTra.getLoaiDoiTra())) {
            for (DoiTraChiTiet dtct : doiTra.getChiTiets()) {
                if (dtct.getIdSpctMoi() != null) {
                    SanPhamChiTiet sm = sanPhamChiTietRepository.findById(dtct.getIdSpctMoi()).orElse(null);
                    if (sm != null) { sm.setSoLuong(sm.getSoLuong() + dtct.getSoLuongTra()); sanPhamChiTietRepository.save(sm); }
                }
            }
        }
        doiTra.setTrangThai("CANCELLED"); doiTra.setGhiChuAdmin(ghiChu);
        DoiTra saved = doiTraRepository.save(doiTra);

        // Trả lại trạng thái DA_GIAO cho hóa đơn nếu không còn yêu cầu nào khác đang xử lý
        HoaDon hd = doiTra.getHoaDon();
        List<DoiTra> otherRequests = doiTraRepository.findByHoaDonId(hd.getId());
        boolean stillProcessing = otherRequests.stream()
                .anyMatch(r -> !r.getId().equals(id) && !"HOAN_THANH".equals(r.getTrangThai()) && !"TU_CHOI".equals(r.getTrangThai()) && !"CANCELLED".equals(r.getTrangThai()));
        
        if (!stillProcessing) {
            checkAndUpdateHoaDonFullRefund(hd.getId());
        }

        return saved;
    }

    private void checkAndUpdateHoaDonFullRefund(Integer hoaDonId) {
        HoaDon hd = hoaDonRepository.findById(hoaDonId).orElse(null);
        if (hd == null) return;

        List<HoaDonChiTiet> listHdct = hoaDonChiTietRepository.findByHoaDonId(hoaDonId);
        int tongMua = listHdct.stream().mapToInt(HoaDonChiTiet::getSoLuong).sum();

        List<DoiTra> listDoiTra = doiTraRepository.findByHoaDonId(hoaDonId);
        int tongTraSuccess = 0;
        BigDecimal tongTienDaHoan = BigDecimal.ZERO;

        for (DoiTra dt : listDoiTra) {
            if ("HOAN_THANH".equals(dt.getTrangThai())) {
                tongTraSuccess += dt.getChiTiets().stream().mapToInt(DoiTraChiTiet::getSoLuongTra).sum();
                // Nếu là hoàn tiền thì mới trừ doanh thu hóa đơn
                if ("REFUND".equals(dt.getLoaiDoiTra()) && dt.getTongTienHoan() != null) {
                    tongTienDaHoan = tongTienDaHoan.add(dt.getTongTienHoan());
                }
            }
        }

        // Cập nhật doanh thu thực tế (Tổng thanh toán ban đầu - Tiền đã hoàn)
        // Lưu ý: Chỉ cập nhật nếu có hoàn tiền để tránh sai lệch do đổi hàng
        if (tongTienDaHoan.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal tongThanhToanGoc = hd.getTongTienHang().subtract(hd.getTienGiam() != null ? hd.getTienGiam() : BigDecimal.ZERO).add(hd.getPhiVanChuyen() != null ? hd.getPhiVanChuyen() : BigDecimal.ZERO);
            hd.setTongThanhToan(tongThanhToanGoc.subtract(tongTienDaHoan).max(BigDecimal.ZERO));
        }

        if (tongTraSuccess >= tongMua) {
            hd.setTrangThaiDon("HOAN_TRA");
        } else if (tongTraSuccess > 0) {
            hd.setTrangThaiDon("HOAN_TRA_MOT_PHAN");
        } else {
            hd.setTrangThaiDon("DA_GIAO");
        }
        hoaDonRepository.save(hd);
    }
}
