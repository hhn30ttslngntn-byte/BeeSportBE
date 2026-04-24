package com.example.sport_be.service;

import com.example.sport_be.entity.HoaDon;
import com.example.sport_be.entity.SanPham;
import com.example.sport_be.entity.SanPhamChiTiet;
import com.example.sport_be.repository.HoaDonRepository;
import com.example.sport_be.repository.SanPhamRepository;
import com.example.sport_be.repository.SanPhamChiTietRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Pattern ORDER_CODE_PATTERN = Pattern.compile("\\bHD[- ]?(\\d+)\\b", Pattern.CASE_INSENSITIVE);
    private static final Set<String> STOP_WORDS = new HashSet<>(List.of(
            "toi", "minh", "muon", "can", "tim", "hoi", "ve", "cho", "xin", "tu", "van",
            "san", "pham", "shop", "cua", "hang", "la", "co", "khong", "mot", "nhung",
            "the", "nao", "gi", "giup", "kiem", "tra", "don", "nay", "kia", "voi", "nhe"
    ));

    private final SanPhamRepository sanPhamRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final HoaDonRepository hoaDonRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.key:}")
    private String apiKey;

    public String generateResponse(String userMessage, List<Map<String, String>> history) {
        String cleanedMessage = safeTrim(userMessage);
        if (cleanedMessage.isBlank()) {
            return "Bạn chưa nhập nội dung. Hãy gửi câu hỏi về sản phẩm, đơn hàng hoặc chính sách đổi trả.";
        }

        List<SanPham> matchedProducts = findRelevantProducts(cleanedMessage);
        Optional<HoaDon> matchedOrder = findOrderFromMessage(cleanedMessage);
        String fallbackAnswer = buildFallbackResponse(cleanedMessage, matchedProducts, matchedOrder);

        if (!StringUtils.hasText(apiKey)) {
            return fallbackAnswer;
        }

        String systemPrompt = buildSystemPrompt(cleanedMessage, matchedProducts, matchedOrder);
        String aiAnswer = callGemini(systemPrompt, cleanedMessage, history);
        return StringUtils.hasText(aiAnswer) ? aiAnswer : fallbackAnswer;
    }

    private String buildSystemPrompt(String userMessage, List<SanPham> matchedProducts, Optional<HoaDon> matchedOrder) {
        return """
                Bạn là Bee Bot AI, một trợ lý AI đa năng của BeeSport.

                NGUYÊN TẮC:
                - Trả lời bằng tiếng Việt tự nhiên, rõ ràng, đúng trọng tâm.
                - Có thể trả lời câu hỏi chung như một AI assistant bình thường.
                - Khi câu hỏi liên quan BeeSport, sản phẩm, đơn hàng, đổi trả, thanh toán hoặc vận chuyển, hãy ưu tiên dữ liệu BeeSport được cung cấp.
                - Nếu người dùng hỏi sản phẩm, ưu tiên gợi ý từ dữ liệu sản phẩm được cung cấp.
                - Khi nhắc đến sản phẩm BeeSport, ưu tiên chèn theo định dạng [PRODUCT:id:tên sản phẩm].
                - Nếu hỏi đơn hàng và có dữ liệu đơn, chỉ trả lời dựa trên dữ liệu đó, không bịa trạng thái.
                - Nếu câu hỏi là kiến thức chung ngoài phạm vi BeeSport, vẫn trả lời trong khả năng như một trợ lý AI.
                - Nếu không chắc chắn về dữ liệu thời gian thực hoặc dữ liệu không có trong hệ thống, nói rõ giới hạn.

                THÔNG TIN CỬA HÀNG:
                - Hotline: 0363 652 758
                - Email: support@beesport.com
                - Chính sách đổi trả: hỗ trợ trong 7 ngày nếu sản phẩm lỗi hoặc sai mô tả.

                CÂU HỎI NGƯỜI DÙNG:
                %s

                BỐI CẢNH ĐƠN HÀNG:
                %s

                DANH SÁCH SẢN PHẨM CÓ THỂ TƯ VẤN:
                %s
                """.formatted(
                userMessage,
                matchedOrder.map(this::formatOrderContext).orElse("Không có đơn hàng phù hợp trong tin nhắn."),
                formatProductsForPrompt(matchedProducts)
        );
    }

    private String buildFallbackResponse(String userMessage, List<SanPham> matchedProducts, Optional<HoaDon> matchedOrder) {
        String normalized = normalize(userMessage);

        if (isGreeting(normalized)) {
            return "Xin chào, mình là Bee Bot. Mình có thể hỗ trợ tìm sản phẩm, kiểm tra đơn hàng hoặc trò chuyện cùng bạn.";
        }

        if (matchedOrder.isPresent()) {
            return buildOrderAnswer(matchedOrder.get());
        }

        if (mentionsOutOfStock(normalized)) {
            return buildInventoryAnswer(true, matchedProducts);
        }

        if (mentionsInStock(normalized)) {
            return buildInventoryAnswer(false, matchedProducts);
        }

        if (mentionsReturnPolicy(normalized)) {
            return """
                    BeeSport hỗ trợ đổi trả trong 7 ngày nếu sản phẩm lỗi hoặc không đúng mô tả.
                    Bạn nên chuẩn bị mã đơn và hình ảnh sản phẩm để xử lý nhanh hơn.
                    """.trim();
        }

        if (mentionsContact(normalized)) {
            return """
                    Bạn có thể liên hệ BeeSport qua:
                    - Hotline: 0363 652 758
                    - Email: support@beesport.com
                    """.trim();
        }

        if (mentionsShipping(normalized)) {
            return "BeeSport có hỗ trợ giao hàng. Nếu cần, mình có thể gợi ý sản phẩm trước rồi bạn kiểm tra phí vận chuyển ở bước thanh toán.";
        }

        if (mentionsPayment(normalized)) {
            return "BeeSport hỗ trợ thanh toán khi nhận hàng và thanh toán trực tuyến. Khi đặt hàng, hệ thống sẽ hiển thị phương thức phù hợp để bạn chọn.";
        }

        if (!matchedProducts.isEmpty()) {
            return buildProductSuggestionAnswer(userMessage, matchedProducts);
        }

        return buildGeneralFallbackAnswer(normalized);
    }

    private String buildOrderAnswer(HoaDon order) {
        String customerName = StringUtils.hasText(order.getTenNguoiNhan()) ? order.getTenNguoiNhan() : "khách hàng";
        String createdAt = order.getNgayTao() != null ? order.getNgayTao().format(DATE_TIME_FORMATTER) : "chưa rõ";
        return """
                Mình đã kiểm tra đơn %s cho %s.
                - Trạng thái: %s
                - Tổng thanh toán: %s
                - Ngày tạo: %s
                Nếu bạn cần, mình có thể giải thích thêm trạng thái đơn này.
                """.formatted(
                order.getMaHoaDon(),
                customerName,
                formatStatus(order.getTrangThaiDon()),
                formatPrice(order.getTongThanhToan()),
                createdAt
        ).trim();
    }

    private String buildProductSuggestionAnswer(String userMessage, List<SanPham> matchedProducts) {
        String intro = inferProductIntro(userMessage);
        String products = matchedProducts.stream()
                .limit(3)
                .map(this::formatProductCard)
                .collect(Collectors.joining("\n"));
        return (intro + "\n" + products + "\nBạn muốn mình lọc tiếp theo giá, thương hiệu hay loại sản phẩm?").trim();
    }

    private String buildInventoryAnswer(boolean outOfStockOnly, List<SanPham> matchedProducts) {
        List<SanPham> productsToCheck = matchedProducts.isEmpty()
                ? sanPhamRepository.findByTrangThaiTrue().stream().limit(12).collect(Collectors.toList())
                : matchedProducts.stream().limit(12).collect(Collectors.toList());

        List<SanPham> filtered = productsToCheck.stream()
                .filter(product -> outOfStockOnly ? getTotalStock(product) <= 0 : getTotalStock(product) > 0)
                .limit(5)
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            return outOfStockOnly
                    ? "Hiện mình chưa thấy sản phẩm nào trong nhóm bạn hỏi đang hết hàng."
                    : "Hiện mình chưa tìm thấy sản phẩm còn hàng phù hợp với mô tả của bạn.";
        }

        String intro = outOfStockOnly
                ? "Mình tìm được một số sản phẩm đang hết hàng:"
                : "Mình tìm được một số sản phẩm hiện còn hàng:";

        String productLines = filtered.stream()
                .map(product -> {
                    int stock = getTotalStock(product);
                    String stockText = outOfStockOnly ? "Hết hàng" : "Còn " + stock + " sản phẩm";
                    return formatProductCard(product) + " - " + stockText;
                })
                .collect(Collectors.joining("\n"));

        return intro + "\n" + productLines;
    }

    private String buildGeneralFallbackAnswer(String normalized) {
        if (normalized.contains("ban la ai")) {
            return "Mình là Bee Bot, trợ lý ảo của BeeSport. Mình có thể hỗ trợ sản phẩm, đơn hàng và trả lời nhanh các câu hỏi chung.";
        }
        if (normalized.contains("cam on")) {
            return "Không có gì. Nếu cần, mình hỗ trợ tiếp ngay.";
        }
        if (normalized.contains("noi chuyen") || normalized.contains("ke chuyen")) {
            return "Được. Bạn muốn nói về thể thao, mua sắm hay cần mình tư vấn gì cụ thể hơn?";
        }
        if (normalized.contains("thoi tiet")) {
            return "Mình chưa có dữ liệu thời tiết thời gian thực. Nhưng nếu bạn muốn, mình có thể gợi ý đồ phù hợp cho trời nắng, mưa hoặc vận động ngoài trời.";
        }
        if (normalized.contains("hoc") || normalized.contains("lap trinh") || normalized.contains("toan")) {
            return "Mình có thể trả lời các câu hỏi chung ở mức cơ bản, nhưng để trả lời kiểu AI đầy đủ như ChatGPT thì backend cần kết nối ổn định tới model AI bên ngoài. Hiện tại mình vẫn sẽ cố trả lời tốt nhất trong khả năng.";
        }

        return """
                Mình có thể trả lời câu hỏi chung, nhưng để trả lời kiểu AI đầy đủ cho mọi chủ đề thì cần kết nối model AI online hoạt động ổn định.
                Hiện tại mình vẫn hỗ trợ tốt nhất với câu hỏi về BeeSport và các câu hỏi chung đơn giản.
                Nếu bạn muốn câu trả lời sát hơn, hãy hỏi cụ thể hơn một chút.
                Ví dụ:
                - “Gợi ý giúp mình đôi giày chạy bộ”
                - “Đơn HD12345 đang ở đâu?”
                - “Shop có hỗ trợ đổi trả không?”
                """.trim();
    }

    private String inferProductIntro(String userMessage) {
        String normalized = normalize(userMessage);
        if (normalized.contains("giay")) {
            return "Mình thấy bạn đang quan tâm đến giày. Đây là vài sản phẩm phù hợp:";
        }
        if (normalized.contains("ao")) {
            return "Mình thấy bạn đang tìm áo thể thao. Bạn có thể xem các mẫu này:";
        }
        if (normalized.contains("quan")) {
            return "Mình thấy bạn đang tìm quần thể thao. Đây là vài gợi ý:";
        }
        return "Mình gợi ý cho bạn một vài sản phẩm đang có trên BeeSport:";
    }

    private Optional<HoaDon> findOrderFromMessage(String message) {
        Matcher matcher = ORDER_CODE_PATTERN.matcher(message);
        while (matcher.find()) {
            String digits = matcher.group(1);
            List<String> candidates = List.of("HD" + digits, "HD-" + digits);
            for (String candidate : candidates) {
                Optional<HoaDon> order = hoaDonRepository.findByMaHoaDon(candidate);
                if (order.isPresent()) {
                    return order;
                }
            }
        }
        return Optional.empty();
    }

    private List<SanPham> findRelevantProducts(String message) {
        String normalizedMessage = normalize(message);
        List<String> keywords = extractKeywords(normalizedMessage);
        List<SanPham> activeProducts = sanPhamRepository.findByTrangThaiTrue();

        if (activeProducts.isEmpty()) {
            return List.of();
        }

        List<SanPham> scoredProducts = activeProducts.stream()
                .filter(product -> scoreProduct(product, normalizedMessage, keywords) > 0)
                .sorted(Comparator
                        .comparingInt((SanPham product) -> scoreProduct(product, normalizedMessage, keywords))
                        .reversed()
                        .thenComparing(product -> safeTrim(product.getTenSanPham())))
                .limit(6)
                .collect(Collectors.toList());

        if (!scoredProducts.isEmpty()) {
            return scoredProducts;
        }

        return activeProducts.stream()
                .sorted(Comparator.comparing(product -> safeTrim(product.getTenSanPham())))
                .limit(6)
                .collect(Collectors.toList());
    }

    private int scoreProduct(SanPham product, String normalizedMessage, List<String> keywords) {
        String productText = buildProductSearchText(product);
        int score = 0;

        for (String keyword : keywords) {
            if (productText.contains(keyword)) {
                score += keyword.length() >= 4 ? 3 : 1;
            }
        }

        String productName = normalize(safeTrim(product.getTenSanPham()));
        if (!normalizedMessage.isBlank() && productName.contains(normalizedMessage)) {
            score += 6;
        }

        return score;
    }

    private int getTotalStock(SanPham product) {
        List<SanPhamChiTiet> details = sanPhamChiTietRepository.findBySanPhamId(product.getId());
        return details.stream()
                .filter(detail -> detail.getTrangThai() == null || detail.getTrangThai())
                .mapToInt(detail -> detail.getSoLuong() == null ? 0 : detail.getSoLuong())
                .sum();
    }

    private List<String> extractKeywords(String normalizedMessage) {
        if (normalizedMessage.isBlank()) {
            return List.of();
        }

        Set<String> keywords = new LinkedHashSet<>();
        for (String part : normalizedMessage.split("\\s+")) {
            String token = safeTrim(part);
            if (token.length() < 2 || STOP_WORDS.contains(token)) {
                continue;
            }
            keywords.add(token);
        }
        return new ArrayList<>(keywords);
    }

    private String buildProductSearchText(SanPham product) {
        StringBuilder builder = new StringBuilder();
        builder.append(normalize(safeTrim(product.getTenSanPham()))).append(' ');
        builder.append(normalize(safeTrim(product.getMa()))).append(' ');

        if (product.getDanhMuc() != null) {
            builder.append(normalize(safeTrim(product.getDanhMuc().getTen()))).append(' ');
        }
        if (product.getThuongHieu() != null) {
            builder.append(normalize(safeTrim(product.getThuongHieu().getTenThuongHieu()))).append(' ');
        }
        if (product.getChatLieu() != null) {
            builder.append(normalize(safeTrim(product.getChatLieu().getTen()))).append(' ');
        }

        return builder.toString();
    }

    private String callGemini(String systemPrompt, String userMessage, List<Map<String, String>> history) {
        try {
            JSONArray contents = new JSONArray();

            if (history != null) {
                for (Map<String, String> msg : history) {
                    String role = "user".equalsIgnoreCase(safeTrim(msg.get("role"))) ? "user" : "model";
                    String content = safeTrim(msg.get("content"));
                    if (content.isBlank()) {
                        continue;
                    }

                    contents.put(new JSONObject()
                            .put("role", role)
                            .put("parts", new JSONArray().put(new JSONObject().put("text", content))));
                }
            }

            contents.put(new JSONObject()
                    .put("role", "user")
                    .put("parts", new JSONArray().put(new JSONObject().put("text", userMessage))));

            JSONObject requestBody = new JSONObject()
                    .put("contents", contents)
                    .put("systemInstruction", new JSONObject()
                            .put("parts", new JSONArray().put(new JSONObject().put("text", systemPrompt))))
                    .put("generationConfig", new JSONObject()
                            .put("temperature", 0.8)
                            .put("topP", 0.95)
                            .put("maxOutputTokens", 512));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

            String response = restTemplate.postForObject(GEMINI_URL + apiKey, entity, String.class);
            if (!StringUtils.hasText(response)) {
                return null;
            }

            JSONObject json = new JSONObject(response);
            JSONArray candidates = json.optJSONArray("candidates");
            if (candidates == null || candidates.length() == 0) {
                return null;
            }

            JSONObject content = candidates.getJSONObject(0).optJSONObject("content");
            if (content == null) {
                return null;
            }

            JSONArray parts = content.optJSONArray("parts");
            if (parts == null || parts.length() == 0) {
                return null;
            }

            return safeTrim(parts.getJSONObject(0).optString("text"));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String formatProductsForPrompt(List<SanPham> products) {
        if (products.isEmpty()) {
            return "Hiện chưa có sản phẩm phù hợp để gợi ý.";
        }

        return products.stream()
                .map(product -> "- " + formatProductCard(product))
                .collect(Collectors.joining("\n"));
    }

    private String formatProductCard(SanPham product) {
        return "[PRODUCT:%d:%s] - Giá %s".formatted(
                product.getId(),
                safeTrim(product.getTenSanPham()),
                formatPrice(product.getGiaGoc())
        );
    }

    private String formatOrderContext(HoaDon order) {
        return """
                - Mã đơn: %s
                - Người nhận: %s
                - Trạng thái: %s
                - Tổng thanh toán: %s
                - Ngày tạo: %s
                """.formatted(
                safeTrim(order.getMaHoaDon()),
                safeTrim(order.getTenNguoiNhan()),
                formatStatus(order.getTrangThaiDon()),
                formatPrice(order.getTongThanhToan()),
                order.getNgayTao() != null ? order.getNgayTao().format(DATE_TIME_FORMATTER) : "chưa rõ"
        ).trim();
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "chưa cập nhật";
        }
        return price.stripTrailingZeros().toPlainString() + " VNĐ";
    }

    private String formatStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "Không xác định";
        }

        return switch (status) {
            case "CHO_XAC_NHAN" -> "Chờ xác nhận";
            case "DA_XAC_NHAN" -> "Đã xác nhận";
            case "DANG_GIAO" -> "Đang giao hàng";
            case "DA_GIAO" -> "Đã giao hàng";
            case "DA_HUY" -> "Đã hủy";
            case "HOAN_THANH" -> "Hoàn thành";
            case "HOAN_TRA" -> "Hoàn trả";
            default -> status;
        };
    }

    private boolean isGreeting(String normalized) {
        return normalized.matches(".*\\b(xin chao|chao|hello|hi|alo|helo|helu)\\b.*");
    }

    private boolean mentionsReturnPolicy(String normalized) {
        return normalized.contains("doi tra")
                || normalized.contains("hoan tra")
                || normalized.contains("bao hanh")
                || normalized.contains("chinh sach");
    }

    private boolean mentionsContact(String normalized) {
        return normalized.contains("hotline")
                || normalized.contains("so dien thoai")
                || normalized.contains("lien he")
                || normalized.contains("email")
                || normalized.contains("dia chi");
    }

    private boolean mentionsShipping(String normalized) {
        return normalized.contains("giao hang")
                || normalized.contains("van chuyen")
                || normalized.contains("ship");
    }

    private boolean mentionsPayment(String normalized) {
        return normalized.contains("thanh toan")
                || normalized.contains("cod")
                || normalized.contains("vnpay")
                || normalized.contains("chuyen khoan");
    }

    private boolean mentionsOutOfStock(String normalized) {
        return normalized.contains("het hang")
                || normalized.contains("chai hang")
                || normalized.contains("khong con hang")
                || normalized.contains("sold out");
    }

    private boolean mentionsInStock(String normalized) {
        return normalized.contains("con hang")
                || normalized.contains("ton kho")
                || normalized.contains("con bao nhieu")
                || normalized.contains("so luong con");
    }

    private String normalize(String value) {
        String text = safeTrim(value).toLowerCase(Locale.ROOT);
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd');
        return normalized.replaceAll("[^a-z0-9\\s-]", " ").replaceAll("\\s+", " ").trim();
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
