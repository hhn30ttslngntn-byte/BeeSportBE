package com.example.sport_be.task;

import com.example.sport_be.entity.HoaDon;
import com.example.sport_be.entity.LichSuHoaDon;
import com.example.sport_be.repository.HoaDonRepository;
import com.example.sport_be.repository.LichSuHoaDonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderAutoConfirmTask {

    private final HoaDonRepository hoaDonRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;

    /**
     * Tự động chuyển trạng thái từ Đang giao (DANG_GIAO) sang Đã giao (DA_GIAO)
     * nếu sau 3 ngày khách hàng không nhấn xác nhận.
     * Chạy mỗi giờ một lần.
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void autoConfirmOrders() {
        log.info("Bắt đầu quét đơn hàng để tự động xác nhận nhận hàng...");
        
        List<HoaDon> shippingOrders = hoaDonRepository.findByTrangThaiDonAndNgayGiaoNotNull("DANG_GIAO");
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        for (HoaDon hd : shippingOrders) {
            if (hd.getNgayGiao() != null && hd.getNgayGiao().isBefore(threeDaysAgo)) {
                log.info("Tự động xác nhận đơn hàng: {}", hd.getMaHoaDon());
                
                hd.setTrangThaiDon("DA_GIAO");
                hd.setNgayCapNhat(LocalDateTime.now());
                hoaDonRepository.save(hd);

                // Ghi log lịch sử
                LichSuHoaDon history = new LichSuHoaDon();
                history.setHoaDon(hd);
                history.setTrangThaiCu("DANG_GIAO");
                history.setTrangThaiMoi("DA_GIAO");
                history.setLoaiHanhDong("AUTO_CONFIRM");
                history.setHanhDong("Hệ thống tự động xác nhận đã nhận hàng sau 3 ngày");
                lichSuHoaDonRepository.save(history);
            }
        }
        
        log.info("Kết thúc quét đơn hàng.");
    }
}
