package com.example.sport_be.service;

import com.example.sport_be.entity.NguoiDung;
import com.example.sport_be.entity.VaiTro;
import com.example.sport_be.repository.NguoiDungRepository;
import com.example.sport_be.repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;

    public NguoiDung register(NguoiDung user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new RuntimeException("Email là bắt buộc!");
        }
        if (user.getMatKhau() == null || user.getMatKhau().isBlank()) {
            throw new RuntimeException("Mật khẩu là bắt buộc!");
        }
        if (user.getHoTen() == null || user.getHoTen().isBlank()) {
            throw new RuntimeException("Họ tên là bắt buộc!");
        }
        if (user.getSoDienThoai() == null || user.getSoDienThoai().isBlank()) {
            throw new RuntimeException("Số điện thoại là bắt buộc!");
        }

        if (nguoiDungRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email đã tồn tại!");
        }
        if (nguoiDungRepository.existsBySoDienThoai(user.getSoDienThoai())) {
            throw new RuntimeException("Số điện thoại đã tồn tại!");
        }

        // Set default role (USER)
        java.util.List<VaiTro> roles = vaiTroRepository.findByMa("USER");
        VaiTro userRole;
        if (!roles.isEmpty()) {
            userRole = roles.get(0);
        } else {
            VaiTro newRole = new VaiTro();
            newRole.setMa("USER");
            newRole.setTen("Khách hàng");
            newRole.setTrangThai(true);
            userRole = vaiTroRepository.save(newRole);
        }
        
        user.setVaiTro(userRole);
        user.setTrangThai(true);
        // Tự động sinh mã người dùng khi đăng ký online
        if (user.getMaNguoiDung() == null || user.getMaNguoiDung().isBlank()) {
            user.setMaNguoiDung("KH" + System.currentTimeMillis());
        }
        // In a real app, hash the password here
        return nguoiDungRepository.save(user);
    }

    public NguoiDung login(String identifier, String password) {
        java.util.List<NguoiDung> users = nguoiDungRepository.findByEmail(identifier);
        if (users.isEmpty()) {
            users = nguoiDungRepository.findBySoDienThoai(identifier);
        }

        if (!users.isEmpty()) {
            NguoiDung user = users.get(0);
            // In a real app, use password encoder
            if (user.getMatKhau() != null && user.getMatKhau().equals(password)) {
                if (!user.getTrangThai()) {
                    throw new RuntimeException("Account is disabled");
                }
                return user;
            }
        }
        throw new RuntimeException("Invalid email/phone or password");
    }

    public void resetPassword(String email, String newPassword) {
        java.util.List<NguoiDung> users = nguoiDungRepository.findByEmail(email);
        if (users.isEmpty()) {
            throw new RuntimeException("Email not found");
        }
        
        NguoiDung user = users.get(0);
        user.setMatKhau(newPassword);
        nguoiDungRepository.save(user);
    }
}
