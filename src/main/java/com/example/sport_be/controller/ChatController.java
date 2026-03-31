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

    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=";

    @PostMapping("/chat")
    public ResponseEntity<String> askAI(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tin nhắn không được để trống");
        }
        
        try {
            // Tìm kiếm sản phẩm liên quan trong DB dựa trên từ khóa người dùng
            List<SanPham> relevantProducts = sanPhamRepository.findByTenSanPhamContainingIgnoreCaseAndTrangThaiTrue(userMessage);
            
            // Nếu không tìm thấy sản phẩm cụ thể, lấy ngẫu nhiên 5 sản phẩm đang kinh doanh để AI có thể gợi ý
            if (relevantProducts.isEmpty()) {
                relevantProducts = sanPhamRepository.findAll().stream()
                    .filter(SanPham::getTrangThai)
                    .limit(5)
                    .collect(Collectors.toList());
            }
            
            String productContext = relevantProducts.isEmpty() ? "Không có sản phẩm nào trong hệ thống hiện tại." : 
                "\nDANH SÁCH SẢN PHẨM THỰC TẾ TRONG CƠ SỞ DỮ LIỆU (CHỈ ĐƯỢC PHÉP GỢI Ý TỪ ĐÂY):\n" + 
                relevantProducts.stream()
                    .map(p -> "- ID: " + p.getId() + ", Tên: " + p.getTenSanPham() + ", Giá: " + p.getGiaGoc() + " VNĐ")
                    .collect(Collectors.joining("\n"));

            String systemPrompt = "Bạn là Bee Bot AI, trợ lý ảo cao cấp của cửa hàng BeeSport. " +
                "PHONG CÁCH PHỤC VỤ: Chuyên nghiệp, thân thiện, am hiểu về thời trang thể thao và luôn sẵn lòng giúp đỡ. " +
                "KHẢ NĂNG: Bạn có thể trả lời bất cứ câu hỏi nào của khách hàng (từ chào hỏi, tư vấn sản phẩm đến các kiến thức thể thao chung). " +
                "QUY TẮC SẢN PHẨM: " +
                "1. Khi tư vấn hoặc giới thiệu sản phẩm, BẮT BUỘC chỉ được dùng các sản phẩm có trong danh sách thực tế dưới đây. " +
                "2. Sử dụng định dạng [PRODUCT:id:tên] để tạo liên kết sản phẩm. " +
                "3. Nếu không có sản phẩm chính xác khách yêu cầu, hãy khéo léo gợi ý sản phẩm tương tự từ danh sách. " +
                "4. Nếu khách hỏi về các vấn đề ngoài sản phẩm, hãy trả lời thông minh và tự nhiên, sau đó có thể dẫn dắt khéo léo về niềm đam mê thể thao. " +
                productContext + 
                "\n\nCâu hỏi của khách hàng: " + userMessage;

            if (apiKey == null || apiKey.isEmpty()) {
                System.out.println("ChatController ERROR: API Key is missing");
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
            String response;
            try {
                response = restTemplate.postForObject(fullUrl, requestBody, String.class);
            } catch (org.springframework.web.client.HttpStatusCodeException e) {
                System.out.println("Gemini API HTTP Error: " + e.getResponseBodyAsString());
                return ResponseEntity.status(e.getStatusCode()).body("Lỗi từ Gemini API: " + e.getResponseBodyAsString());
            } catch (Exception e) {
                System.out.println("Gemini connection error: " + e.getMessage());
                return ResponseEntity.status(500).body("Lỗi khi kết nối với Gemini API: " + e.getMessage());
            }

            if (response == null) {
                return ResponseEntity.status(500).body("Gemini API không phản hồi.");
            }

            JSONObject jsonResponse = new JSONObject(response);
            
            if (jsonResponse.has("candidates") && !jsonResponse.getJSONArray("candidates").isEmpty()) {
                JSONObject firstCandidate = jsonResponse.getJSONArray("candidates").getJSONObject(0);
                if (firstCandidate.has("content") && firstCandidate.getJSONObject("content").has("parts")) {
                    String botAnswer = firstCandidate.getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");
                    return ResponseEntity.ok(botAnswer);
                }
            }
            return ResponseEntity.status(500).body("Định dạng phản hồi từ AI không hợp lệ.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi Backend: " + e.getMessage());
        }
    }
}
