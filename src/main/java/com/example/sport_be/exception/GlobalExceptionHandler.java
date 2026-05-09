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

        if (message != null && (message.contains("Vuot qua ton kho") || message.contains("Khong du ton kho"))) {
            response.put("error", "San pham khong du so luong trong kho");
            return ResponseEntity.badRequest().body(response);
        }

        if (e instanceof RuntimeException) {
            response.put("error", message != null ? message : "Yeu cau khong hop le");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("error", message != null ? message : "Da co loi xay ra");
        return ResponseEntity.internalServerError().body(response);
    }
}
