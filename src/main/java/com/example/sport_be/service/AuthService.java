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
        if (nguoiDungRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (nguoiDungRepository.existsBySoDienThoai(user.getSoDienThoai())) {
            throw new RuntimeException("Phone number already exists");
        }

        // Set default role (USER)
        VaiTro userRole = vaiTroRepository.findByMa("USER")
                .orElseGet(() -> {
                    VaiTro newRole = new VaiTro();
                    newRole.setMa("USER");
                    newRole.setTen("Khách hàng");
                    newRole.setTrangThai(true);
                    return vaiTroRepository.save(newRole);
                });
        
        user.setVaiTro(userRole);
        user.setTrangThai(true);
        // In a real app, hash the password here
        return nguoiDungRepository.save(user);
    }

    public NguoiDung login(String identifier, String password) {
        Optional<NguoiDung> userOpt = nguoiDungRepository.findByEmail(identifier);
        if (userOpt.isEmpty()) {
            userOpt = nguoiDungRepository.findBySoDienThoai(identifier);
        }

        if (userOpt.isPresent()) {
            NguoiDung user = userOpt.get();
            // In a real app, use password encoder
            if (user.getMatKhau().equals(password)) {
                if (!user.getTrangThai()) {
                    throw new RuntimeException("Account is disabled");
                }
                return user;
            }
        }
        throw new RuntimeException("Invalid email/phone or password");
    }

    public void resetPassword(String email, String newPassword) {
        NguoiDung user = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        
        user.setMatKhau(newPassword);
        nguoiDungRepository.save(user);
    }
}
