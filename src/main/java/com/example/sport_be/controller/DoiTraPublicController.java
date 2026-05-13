package com.example.sport_be.controller;

import com.example.sport_be.dto.XacNhanNhanTienRequest;
import com.example.sport_be.service.DoiTraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/doi-tra")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoiTraPublicController {
    private final DoiTraService doiTraService;

    @GetMapping("/confirm-info")
    public ResponseEntity<?> getConfirmInfo(@RequestParam String token) {
        try {
            return ResponseEntity.ok(doiTraService.getConfirmInfo(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/xac-nhan-nhan-tien")
    public ResponseEntity<?> xacNhanNhanTien(@RequestBody XacNhanNhanTienRequest request) {
        try {
            return ResponseEntity.ok(doiTraService.xacNhanNhanTien(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
