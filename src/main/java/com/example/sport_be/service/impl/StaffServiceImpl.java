package com.example.sport_be.service.impl;

import com.example.sport_be.DTO.NguoiDungRequest;
import com.example.sport_be.DTO.NguoiDungResponse;
import com.example.sport_be.model.NguoiDung;
import com.example.sport_be.model.VaiTro;
import com.example.sport_be.repository.RoleRepository;
import com.example.sport_be.repository.UserRepository;
import com.example.sport_be.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private static final Integer STAFF_ROLE_ID = 2;

    @Override
    public List<NguoiDungResponse> getAllStaff() {
        return userRepository.findByVaiTro_Id(STAFF_ROLE_ID)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public NguoiDungResponse createStaff(NguoiDungRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        VaiTro role = roleRepository.findById(STAFF_ROLE_ID)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        NguoiDung user = NguoiDung.builder()
                .vaiTro(role)
                .hoTen(request.getHoTen())
                .email(request.getEmail())
                .matKhau(request.getMatKhau())
                .trangThai(true)
                .ngayTao(LocalDateTime.now())
                .build();

        return toResponse(userRepository.save(user));
    }

    @Override
    public NguoiDungResponse updateStaff(Integer id, NguoiDungRequest request) {

        NguoiDung user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        user.setHoTen(request.getHoTen());
        user.setEmail(request.getEmail());
        user.setMatKhau(request.getMatKhau());

        return toResponse(userRepository.save(user));
    }

    @Override
    public void deleteStaff(Integer id) {

        NguoiDung user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        user.setTrangThai(false);

        userRepository.save(user);
    }

    private NguoiDungResponse toResponse(NguoiDung user) {

        return NguoiDungResponse.builder()
                .id(user.getId())
                .hoTen(user.getHoTen())
                .email(user.getEmail())
                .trangThai(user.getTrangThai())
                .idVaiTro(user.getVaiTro().getId())
                .build();
    }
}
