package com.example.sport_be.service;

import com.example.sport_be.entity.DoiTra;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username:}")
    private String mailUser;

    @Value("${app.frontend-url:http://localhost:5174}")
    private String frontendUrl;

    private final JavaMailSender sender;

    public boolean sendRefundConfirmation(DoiTra dt) {
        if (mailUser == null || mailUser.isBlank() || sender == null) {
            return false;
        }
        try {
            if (dt == null || dt.getHoaDon() == null || dt.getHoaDon().getNguoiDung() == null) {
                return false;
            }
            String to = dt.getHoaDon().getNguoiDung().getEmail();
            if (to == null || to.isBlank()) {
                return false;
            }

            String token = dt.getTokenXacNhan();
            String url = frontendUrl + "/return-confirm?token=" + token;

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject("Xac nhan da nhan tien hoan - " + dt.getMaDoiTra());
            msg.setText("Shop da hoan tien. Vui long xac nhan da nhan tien tai link: " + url);
            sender.send(msg);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
