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

    // 4. Quyết định -> HOAN_THANH hoặc TU_CHOI
    // Body: { "action": "HOAN_TIEN" } hoặc { "action": "TU_CHOI" }
    @PutMapping("/{id}/quyet-dinh")
    public ResponseEntity<?> quyetDinh(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            String action = body.get("action");
            if (action == null || action.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu trường 'action'. Chỉ chấp nhận: HOAN_TIEN hoặc TU_CHOI"));
            }
            return ResponseEntity.ok(doiTraService.quyetDinh(id, action));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
