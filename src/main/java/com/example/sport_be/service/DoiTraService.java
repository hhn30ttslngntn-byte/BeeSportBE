package com.example.sport_be.service;

import com.example.sport_be.entity.*;
import com.example.sport_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoiTraService {
    private final DoiTraRepository doiTraRepository;
    private final DoiTraChiTietRepository doiTraChiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final VNPayService vnPayService;

    /**
     * Lấy danh sách tất cả đổi trả
     */
    public List<DoiTra> getAllDoiTra() {
        return doiTraRepository.findAll();
    }

    /**
     * Lấy chi tiết 1 đổi trả
     */
    public DoiTra getDoiTraById(Integer id) {
        return doiTraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu đổi trả ID=" + id));
    }

    /**
     * Lấy danh sách đổi trả theo hóa đơn
     */
    public List<DoiTra> getDoiTraByHoaDon(Integer hoaDonId) {
        return doiTraRepository.findByHoaDonId(hoaDonId);
    }

    // =============================================
    // 1. XÁC NHẬN -> CHO_GIAO_HANG (Đợi bưu cục)
    // =============================================
    @Transactional
    public DoiTra xacNhan(Integer id) {
        DoiTra doiTra = getDoiTraById(id);

        // Validate trạng thái hiện tại phải là CHO_XAC_NHAN_HOAN
        if (!"CHO_XAC_NHAN_HOAN".equals(doiTra.getTrangThai())) {
            throw new RuntimeException("Chỉ có thể xác nhận yêu cầu đang ở trạng thái CHO_XAC_NHAN_HOAN. "
                    + "Trạng thái hiện tại: " + doiTra.getTrangThai());
        }

        doiTra.setTrangThai("CHO_GIAO_HANG");
        return doiTraRepository.save(doiTra);
    }

    // =============================================
    // 2. TỪ CHỐI -> TU_CHOI (Đóng ticket)
    // =============================================
    @Transactional
    public DoiTra tuChoi(Integer id) {
        DoiTra doiTra = getDoiTraById(id);

        // Cho phép từ chối ở CHO_XAC_NHAN_HOAN hoặc DA_NHAN_HANG_KIEM_TRA
        if (!"CHO_XAC_NHAN_HOAN".equals(doiTra.getTrangThai())
                && !"DA_NHAN_HANG_KIEM_TRA".equals(doiTra.getTrangThai())) {
            throw new RuntimeException("Chỉ có thể từ chối ở trạng thái CHO_XAC_NHAN_HOAN hoặc DA_NHAN_HANG_KIEM_TRA. "
                    + "Trạng thái hiện tại: " + doiTra.getTrangThai());
        }

        doiTra.setTrangThai("TU_CHOI");
        return doiTraRepository.save(doiTra);
    }

    // =============================================
    // 3. ĐÃ NHẬN HÀNG -> DA_NHAN_HANG_KIEM_TRA
    // =============================================
    @Transactional
    public DoiTra daNhanHang(Integer id) {
        DoiTra doiTra = getDoiTraById(id);

        // Validate trạng thái hiện tại phải là CHO_GIAO_HANG
        if (!"CHO_GIAO_HANG".equals(doiTra.getTrangThai())) {
            throw new RuntimeException("Chỉ có thể xác nhận đã nhận hàng khi đang ở trạng thái CHO_GIAO_HANG. "
                    + "Trạng thái hiện tại: " + doiTra.getTrangThai());
        }

        doiTra.setTrangThai("DA_NHAN_HANG_KIEM_TRA");
        return doiTraRepository.save(doiTra);
    }

    // =============================================
    // 4. QUYẾT ĐỊNH -> HOAN_THANH hoặc TU_CHOI
    // =============================================
    @Transactional
    public DoiTra quyetDinh(Integer id, String action) {
        DoiTra doiTra = getDoiTraById(id);

        // Validate trạng thái hiện tại phải là DA_NHAN_HANG_KIEM_TRA
        if (!"DA_NHAN_HANG_KIEM_TRA".equals(doiTra.getTrangThai())) {
            throw new RuntimeException("Chỉ có thể quyết định khi ở trạng thái DA_NHAN_HANG_KIEM_TRA. "
                    + "Trạng thái hiện tại: " + doiTra.getTrangThai());
        }

        if ("TU_CHOI".equals(action)) {
            // --- Nhánh TỪ CHỐI ---
            doiTra.setTrangThai("TU_CHOI");
            return doiTraRepository.save(doiTra);

        } else if ("HOAN_TIEN".equals(action)) {
            // --- Nhánh HOÀN TIỀN ---

            // Bước 1: Gọi VNPay hoàn tiền (dummy)
            Boolean refundResult = vnPayService.processRefund(doiTra);
            if (!Boolean.TRUE.equals(refundResult)) {
                // Nếu VNPay trả false -> Exception -> @Transactional Rollback toàn bộ
                throw new RuntimeException("Hoàn tiền qua VNPay thất bại cho đơn đổi trả mã: "
                        + doiTra.getMaDoiTra() + ". Vui lòng thử lại sau.");
            }

            // Bước 2: Cộng lại tồn kho cho từng SPCT
            List<DoiTraChiTiet> chiTiets = doiTraChiTietRepository.findByDoiTraId(id);
            for (DoiTraChiTiet dtct : chiTiets) {
                HoaDonChiTiet hdct = dtct.getHoaDonChiTiet();
                SanPhamChiTiet spct = hdct.getSanPhamChiTiet();

                // Cộng lại số lượng tồn kho
                spct.setSoLuong(spct.getSoLuong() + dtct.getSoLuongTra());

                // Trừ số lượng đã bán (đảm bảo không âm)
                int newSoLuongDaBan = (spct.getSoLuongDaBan() != null ? spct.getSoLuongDaBan() : 0) - dtct.getSoLuongTra();
                spct.setSoLuongDaBan(Math.max(newSoLuongDaBan, 0));

                sanPhamChiTietRepository.save(spct);
            }

            // Bước 3: Đổi trạng thái -> HOAN_THANH
            doiTra.setTrangThai("HOAN_THANH");
            DoiTra saved = doiTraRepository.save(doiTra);

            // Bước 4: Kiểm tra nếu TẤT CẢ sản phẩm trong hóa đơn đã hoàn trả hết
            // -> set trạng thái hóa đơn = HOAN_TRA
            checkAndUpdateHoaDonFullRefund(doiTra.getHoaDon().getId());

            return saved;

        } else {
            throw new RuntimeException("Action không hợp lệ. Chỉ chấp nhận: HOAN_TIEN hoặc TU_CHOI");
        }
    }

    /**
     * Kiểm tra nếu toàn bộ sản phẩm trong hóa đơn đã được hoàn trả hết
     * thì cập nhật trạng thái hóa đơn = HOAN_TRA
     */
    private void checkAndUpdateHoaDonFullRefund(Integer hoaDonId) {
        List<HoaDonChiTiet> allItems = hoaDonChiTietRepository.findByHoaDonId(hoaDonId);

        boolean allReturned = true;
        for (HoaDonChiTiet hdct : allItems) {
            Integer totalReturned = doiTraChiTietRepository.sumSoLuongTraByHoaDonChiTietId(hdct.getId());
            if (totalReturned < hdct.getSoLuong()) {
                allReturned = false;
                break;
            }
        }

        if (allReturned) {
            HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID=" + hoaDonId));
            hoaDon.setTrangThaiDon("HOAN_TRA");
            hoaDonRepository.save(hoaDon);
        }
    }
}
