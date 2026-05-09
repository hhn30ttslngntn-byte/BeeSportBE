package com.example.sport_be.controller;

import com.example.sport_be.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat")
    public ResponseEntity<String> askAI(@RequestBody Map<String, Object> payload) {
        String userMessage = payload.get("message") instanceof String value ? value : "";
        List<Map<String, String>> history = payload.get("history") instanceof List<?> value
                ? (List<Map<String, String>>) value
                : List.of();

        if (userMessage.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tin nhắn không được để trống");
        }

        try {
            return ResponseEntity.ok(chatService.generateResponse(userMessage, history));
        } catch (Exception ignored) {
            return ResponseEntity.ok(
                    "Mình vẫn hỗ trợ được các câu hỏi cơ bản, nhưng vừa có lỗi nội bộ tạm thời. Bạn thử hỏi lại ngắn hơn hoặc đổi cách diễn đạt nhé."
            );
        }
    }
}
