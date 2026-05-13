package com.example.sport_be.controller;

import com.example.sport_be.dto.CauHinhDoiTraRequest;
import com.example.sport_be.service.CauHinhDoiTraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/cau-hinh-doi-tra")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CauHinhDoiTraController {
    private final CauHinhDoiTraService cauHinhDoiTraService;

    @GetMapping
    public ResponseEntity<?> getConfig() {
        return ResponseEntity.ok(cauHinhDoiTraService.get());
    }

    @PutMapping
    public ResponseEntity<?> updateConfig(@RequestBody CauHinhDoiTraRequest request) {
        try {
            return ResponseEntity.ok(cauHinhDoiTraService.updateConfig(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
