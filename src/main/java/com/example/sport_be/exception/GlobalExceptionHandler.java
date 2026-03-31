package com.example.sport_be.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        String message = e.getMessage();
        
        // Bắt lỗi RAISERROR từ SQL Server
        if (message != null && (message.contains("Vượt quá tồn kho") || message.contains("Không đủ tồn kho"))) {
            response.put("error", "Sản phẩm không đủ số lượng trong kho");
            return ResponseEntity.badRequest().body(response);
        }
        
        response.put("error", message != null ? message : "Đã có lỗi xảy ra");
        return ResponseEntity.internalServerError().body(response);
    }
}
