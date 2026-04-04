package com.example.sport_be.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Service
public class VNPayService {

    private static final BigDecimal MIN_VNPAY_AMOUNT = BigDecimal.valueOf(5000);
    private static final BigDecimal MAX_VNPAY_AMOUNT = BigDecimal.valueOf(999_999_999);

    @Value("${vnpay.pay-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpayUrl;

    @Value("${vnpay.tmn-code:TCB08L7V}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret:O4T0R3S89Q5N7Y9I1V2X3Z4A5B6C7D8E}")
    private String vnpHashSecret;

    public String createPaymentUrl(
            HttpServletRequest request,
            BigDecimal amount,
            String orderInfo,
            String returnUrl,
            String txnRef
    ) {
        if (amount == null) {
            throw new IllegalArgumentException("So tien thanh toan khong hop le");
        }
        if (txnRef == null || txnRef.isBlank()) {
            throw new IllegalArgumentException("Ma giao dich VNPAY khong hop le");
        }
        if (returnUrl == null || returnUrl.isBlank()) {
            throw new IllegalArgumentException("Return URL VNPAY khong hop le");
        }
        // if (isLocalUrl(returnUrl)) {
        //     throw new IllegalArgumentException("VNPay khong chap nhan localhost lam return URL. Hay dung domain public da dang ky voi VNPay.");
        // }

        BigDecimal normalizedAmount = amount.setScale(0, RoundingMode.HALF_UP);
        if (normalizedAmount.compareTo(MIN_VNPAY_AMOUNT) < 0 || normalizedAmount.compareTo(MAX_VNPAY_AMOUNT) > 0) {
            throw new IllegalArgumentException("So tien thanh toan VNPAY phai tu 5,000 den duoi 1 ty dong");
        }

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Amount", normalizedAmount.multiply(BigDecimal.valueOf(100)).toPlainString());
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_IpAddr", resolveClientIp(request));

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        vnpParams.put("vnp_CreateDate", formatter.format(calendar.getTime()));

        calendar.add(Calendar.MINUTE, 15);
        vnpParams.put("vnp_ExpireDate", formatter.format(calendar.getTime()));

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = vnpParams.get(fieldName);
            if (fieldValue == null || fieldValue.isEmpty()) {
                continue;
            }

            if (hashData.length() > 0) {
                hashData.append('&');
                query.append('&');
            }

            hashData.append(fieldName)
                    .append('=')
                    .append(urlEncode(fieldValue));

            query.append(urlEncode(fieldName))
                    .append('=')
                    .append(urlEncode(fieldValue));
        }

        String vnpSecureHash = hmacSHA512(vnpHashSecret, hashData.toString());
        return vnpayUrl + "?" + query + "&vnp_SecureHash=" + vnpSecureHash;
    }

    public boolean verifyCallback(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return false;
        }

        String receivedSecureHash = params.get("vnp_SecureHash");
        if (receivedSecureHash == null || receivedSecureHash.isBlank()) {
            return false;
        }

        List<String> fieldNames = new ArrayList<>(params.keySet());
        fieldNames.remove("vnp_SecureHash");
        fieldNames.remove("vnp_SecureHashType");
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue == null || fieldValue.isEmpty()) {
                continue;
            }

            if (hashData.length() > 0) {
                hashData.append('&');
            }

            hashData.append(fieldName)
                    .append('=')
                    .append(urlEncode(fieldValue));
        }

        String calculatedHash = hmacSHA512(vnpHashSecret, hashData.toString());
        return calculatedHash.equalsIgnoreCase(receivedSecureHash);
    }

    private boolean isLocalUrl(String url) {
        String normalized = url.toLowerCase();
        return normalized.contains("localhost")
                || normalized.contains("127.0.0.1")
                || normalized.contains("0.0.0.0");
    }

    private String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String remoteAddr = request.getRemoteAddr();
        return (remoteAddr == null || remoteAddr.isBlank()) ? "127.0.0.1" : remoteAddr;
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Khong the ma hoa du lieu VNPAY", e);
        }
    }

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }

            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);

            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * DUMMY - Hoàn tiền qua VNPay cho đơn đổi trả.
     *
     * CẦU DAO CỨU SINH: Nếu gọi VNPay thật bị lỗi, mở comment dòng "return true;"
     * ở đầu hàm để bypass VNPay và rẽ nhánh TRUE tiếp tục lưu DB.
     *
     * @param doiTra đối tượng đổi trả cần hoàn tiền
     * @return true nếu hoàn tiền thành công, false nếu thất bại
     */
    public Boolean processRefund(com.example.sport_be.entity.DoiTra doiTra) {
        // === CẦU DAO CỨU SINH - Mở comment dòng dưới khi VNPay gọi thật bị lỗi ===
        // return true;

        // TODO: Thay thế bằng code gọi VNPay Refund API thật
        // Tham khảo: https://sandbox.vnpayment.vn/apis/docs/truy-van-hoan-tien/
        // Command: refund
        // vnp_TransactionType: "02" (Hoàn tiền toàn phần) hoặc "03" (Hoàn tiền một phần)
        // vnp_Amount: doiTra.getTongTienHoan() * 100

        System.out.println("[VNPay Refund DUMMY] Ma doi tra: " + doiTra.getMaDoiTra()
                + " | Tong tien hoan: " + doiTra.getTongTienHoan());

        // Trả true mặc định cho môi trường DEV (sandbox)
        return true;
    }
}
