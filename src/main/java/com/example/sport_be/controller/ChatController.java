package com.example.sport_be.controller;

import com.example.sport_be.entity.SanPham;
import com.example.sport_be.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ChatController {

    private final SanPhamRepository sanPhamRepository;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    @PostMapping("/chat")
    public ResponseEntity<String> askAI(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        
        // Tìm kiếm sản phẩm liên quan trong DB
        List<SanPham> relevantProducts = sanPhamRepository.findByTenSanPhamContainingIgnoreCaseAndTrangThaiTrue(userMessage);
        
        String productContext = relevantProducts.isEmpty() ? "" : 
            "\nDanh sách sản phẩm phù hợp trong hệ thống:\n" + 
            relevantProducts.stream()
                .map(p -> "- ID: " + p.getId() + ", Tên: " + p.getTenSanPham() + ", Giá: " + p.getGiaGoc())
                .collect(Collectors.joining("\n"));

        String systemPrompt = "Bạn là Bee Bot, trợ lý ảo của cửa hàng BeeSport. " +
            "Nhiệm vụ của bạn là tư vấn và giúp khách hàng tìm kiếm sản phẩm thể thao. " +
            "Khi giới thiệu sản phẩm, BẮT BUỘC phải sử dụng định dạng [PRODUCT:id:tên] để hệ thống hiển thị link. " +
            "Ví dụ: 'Bạn có thể xem sản phẩm [PRODUCT:1:Áo Nike Sport] đang rất hot'. " +
            "Nếu khách hàng hỏi về sản phẩm, hãy dựa vào danh sách dưới đây để trả lời. " +
            "Nếu không tìm thấy sản phẩm cụ thể trong danh sách, hãy tư vấn chung và mời khách hàng xem danh mục sản phẩm. " +
            productContext + 
            "\n\nCâu hỏi của khách hàng: " + userMessage;

        try {
            if (apiKey == null || apiKey.isEmpty()) {
                return ResponseEntity.status(500).body("Lỗi: Chưa cấu hình API Key");
            }

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", systemPrompt)
                    ))
                )
            );

            String fullUrl = GEMINI_URL + apiKey;
            String response = restTemplate.postForObject(fullUrl, requestBody, String.class);

            JSONObject jsonResponse = new JSONObject(response);
            
            if (jsonResponse.has("candidates")) {
                String botAnswer = jsonResponse.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");
                return ResponseEntity.ok(botAnswer);
            } else {
                return ResponseEntity.status(500).body("AI không thể trả lời câu hỏi này.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi Backend: " + e.getMessage());
        }
    }
}
