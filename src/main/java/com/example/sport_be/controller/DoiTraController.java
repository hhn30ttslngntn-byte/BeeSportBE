package com.example.sport_be.controller;

import com.example.sport_be.entity.DoiTra;
import com.example.sport_be.service.DoiTraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/doi-tra")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoiTraController {
    private final DoiTraService doiTraService;

    // Lấy danh sách tất cả đổi trả
    @GetMapping
    public ResponseEntity<List<DoiTra>> getAllDoiTra() {
        return ResponseEntity.ok(doiTraService.getAllDoiTra());
    }

    // Lấy chi tiết 1 đổi trả
    @GetMapping("/{id}")
    public ResponseEntity<DoiTra> getDoiTraById(@PathVariable Integer id) {
        return ResponseEntity.ok(doiTraService.getDoiTraById(id));
    }

    // Lấy danh sách đổi trả theo hóa đơn
    @GetMapping("/hoa-don/{hoaDonId}")
    public ResponseEntity<List<DoiTra>> getDoiTraByHoaDon(@PathVariable Integer hoaDonId) {
        return ResponseEntity.ok(doiTraService.getDoiTraByHoaDon(hoaDonId));
    }

    // 1. Xác nhận -> CHO_GIAO_HANG
    @PutMapping("/{id}/xac-nhan")
    public ResponseEntity<?> xacNhan(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(doiTraService.xacNhan(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. Từ chối -> TU_CHOI
    @PutMapping("/{id}/tu-choi")
    public ResponseEntity<?> tuChoi(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(doiTraService.tuChoi(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. Đã nhận hàng -> DA_NHAN_HANG_KIEM_TRA
    @PutMapping("/{id}/da-nhan-hang")
    public ResponseEntity<?> daNhanHang(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(doiTraService.daNhanHang(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 5. Hủy yêu cầu
    @PutMapping("/{id}/huy")
    public ResponseEntity<?> huyYeuCau(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            String ghiChu = body.get("ghiChu");
            return ResponseEntity.ok(doiTraService.huyYeuCau(id, ghiChu));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 6. Xác nhận đã thanh toán/hoàn tiền thủ công
    @PutMapping("/{id}/xac-nhan-thanh-toan")
    public ResponseEntity<?> xacNhanThanhToan(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(doiTraService.xacNhanThanhToan(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 4. Quyết định -> HOAN_THANH hoặc TU_CHOI
    // Body: { "action": "HOAN_THANH", "chiTietKiemKho": { "id_dtct": true/false }, "ghiChuAdmin": "..." }
    @PutMapping("/{id}/quyet-dinh")
    public ResponseEntity<?> quyetDinh(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            String action = (String) body.get("action");
            if (action == null || action.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu trường 'action'. Chỉ chấp nhận: HOAN_THANH hoặc TU_CHOI"));
            }
            
            // Ép kiểu chiTietKiemKho từ Map<String, Object> sang Map<Integer, Boolean>
            Map<Integer, Boolean> chiTietKiemKho = new java.util.HashMap<>();
            Object ktObj = body.get("chiTietKiemKho");
            if (ktObj instanceof Map) {
                Map<?, ?> rawMap = (Map<?, ?>) ktObj;
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    chiTietKiemKho.put(Integer.valueOf(entry.getKey().toString()), Boolean.valueOf(entry.getValue().toString()));
                }
            }
            
            String ghiChuAdmin = (String) body.get("ghiChuAdmin");
            
            return ResponseEntity.ok(doiTraService.quyetDinh(id, action, chiTietKiemKho, ghiChuAdmin));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Lấy nhật ký theo hóa đơn
    @GetMapping("/logs/hoa-don/{hoaDonId}")
    public ResponseEntity<?> getLogsByHoaDon(@PathVariable Integer hoaDonId) {
        return ResponseEntity.ok(doiTraService.getLogsByHoaDon(hoaDonId));
    }

    // Lấy nhật ký theo yêu cầu đổi trả
    @GetMapping("/logs/doi-tra/{doiTraId}")
    public ResponseEntity<?> getLogsByDoiTra(@PathVariable Integer doiTraId) {
        return ResponseEntity.ok(doiTraService.getLogsByDoiTra(doiTraId));
    }
}
