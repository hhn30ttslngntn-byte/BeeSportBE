package com.example.sport_be.service.impl;

import com.example.sport_be.DTO.NguoiDungResponse;
import com.example.sport_be.model.NguoiDung;
import com.example.sport_be.repository.UserRepository;
import com.example.sport_be.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;

    private static final Integer CUSTOMER_ROLE_ID = 3;

    @Override
    public List<NguoiDungResponse> getAllCustomers() {

        return userRepository.findByVaiTro_Id(CUSTOMER_ROLE_ID)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public NguoiDungResponse getCustomerById(Integer id) {

        NguoiDung user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return toResponse(user);
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
