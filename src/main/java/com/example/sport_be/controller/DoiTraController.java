package com.example.sport_be.controller;

import com.example.sport_be.dto.BenChiuLoiRequest;
import com.example.sport_be.dto.DuyetKiemRequest;
import com.example.sport_be.dto.HoanTienRequest;
import com.example.sport_be.dto.KhachXacNhanThuCongRequest;
import com.example.sport_be.dto.KiemHangRequest;
import com.example.sport_be.dto.QuyetDinhRequest;
import com.example.sport_be.entity.DoiTra;
import com.example.sport_be.service.DoiTraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/doi-tra")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoiTraController {
    private final DoiTraService doiTraService;

    @GetMapping
    public ResponseEntity<List<DoiTra>> getAllDoiTra() {
        return ResponseEntity.ok(doiTraService.getAllDoiTra());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoiTra> getDoiTraById(@PathVariable Integer id) {
        return ResponseEntity.ok(doiTraService.getDoiTraById(id));
    }

    @GetMapping("/hoa-don/{hoaDonId}")
    public ResponseEntity<List<DoiTra>> getDoiTraByHoaDon(@PathVariable Integer hoaDonId) {
        return ResponseEntity.ok(doiTraService.getDoiTraByHoaDon(hoaDonId));
    }

    @PutMapping("/{id}/xac-nhan")
    public ResponseEntity<?> xacNhan(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(doiTraService.xacNhan(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/tu-choi")
    public ResponseEntity<?> tuChoi(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(doiTraService.tuChoi(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/da-nhan-hang")
    public ResponseEntity<?> daNhanHang(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(doiTraService.daNhanHang(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/ben-chiu-loi")
    public ResponseEntity<?> capNhatBenChiuLoi(@PathVariable Integer id, @RequestBody BenChiuLoiRequest request) {
        try {
            return ResponseEntity.ok(doiTraService.updateBenChiuLoi(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/kiem")
    public ResponseEntity<?> kiemHang(@PathVariable Integer id, @RequestBody KiemHangRequest request) {
        try {
            return ResponseEntity.ok(doiTraService.kiemHang(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/duyet-kiem")
    public ResponseEntity<?> duyetKiem(@PathVariable Integer id, @RequestBody DuyetKiemRequest request) {
        try {
            return ResponseEntity.ok(doiTraService.duyetKiem(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/duyet-tra-lai")
    public ResponseEntity<?> duyetTraLai(@PathVariable Integer id, @RequestBody(required = false) Map<String, String> body) {
        try {
            String ghiChu = body != null ? body.get("ghiChu") : null;
            return ResponseEntity.ok(doiTraService.duyetTraLai(id, ghiChu));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDoiTra(@RequestParam("files") MultipartFile[] files,
                                          @RequestParam("doiTraId") Integer doiTraId,
                                          @RequestParam("baseUrl") String baseUrl) {
        try {
            return ResponseEntity.ok(com.example.sport_be.config.FileStorageUtils.saveFiles(files, doiTraId, baseUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/hoan-tien")
    public ResponseEntity<?> hoanTien(@PathVariable Integer id, @RequestBody HoanTienRequest request) {
        try {
            return ResponseEntity.ok(doiTraService.hoanTien(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/huy")
    public ResponseEntity<?> huyYeuCau(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            String ghiChu = body.get("ghiChu");
            return ResponseEntity.ok(doiTraService.huyYeuCau(id, ghiChu));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/khach-xac-nhan-thu-cong")
    public ResponseEntity<?> khachXacNhanThuCong(@PathVariable Integer id, @RequestBody KhachXacNhanThuCongRequest request) {
        try {
            return ResponseEntity.ok(doiTraService.khachXacNhanThuCong(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/quyet-dinh")
    public ResponseEntity<?> quyetDinh(@PathVariable Integer id, @RequestBody QuyetDinhRequest request) {
        try {
            return ResponseEntity.ok(doiTraService.quyetDinh(id, "HOAN_THANH", null, request == null ? null : request.getGhiChuAdmin()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/logs/hoa-don/{hoaDonId}")
    public ResponseEntity<?> getLogsByHoaDon(@PathVariable Integer hoaDonId) {
        return ResponseEntity.ok(doiTraService.getLogsByHoaDon(hoaDonId));
    }

    @GetMapping("/logs/doi-tra/{doiTraId}")
    public ResponseEntity<?> getLogsByDoiTra(@PathVariable Integer doiTraId) {
        return ResponseEntity.ok(doiTraService.getLogsByDoiTra(doiTraId));
    }
}
