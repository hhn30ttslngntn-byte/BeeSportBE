package com.example.sport_be.service;

import com.example.sport_be.DTO.GioHangRequest;
import com.example.sport_be.DTO.GioHangResponse;
import com.example.sport_be.model.GioHang;
import com.example.sport_be.model.NguoiDung;
import com.example.sport_be.repository.GioHangRepository;
import com.example.sport_be.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GioHangService {
    private final GioHangRepository repository;
    private final NguoiDungRepository nguoiDungRepository;

    public GioHangResponse create(GioHangRequest request) {

        NguoiDung user = nguoiDungRepository.findById(request.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("User not found"));

        GioHang entity = new GioHang();
        entity.setNguoiDung(user);
        entity.setTrangThai("DANG_SU_DUNG");
        entity.setNgayTao(LocalDateTime.now());

        return mapToResponse(repository.save(entity));
    }

    private GioHangResponse mapToResponse(GioHang entity) {
        GioHangResponse res = new GioHangResponse();
        res.setId(entity.getId());
        res.setIdNguoiDung(entity.getNguoiDung().getId());
        res.setTrangThai(entity.getTrangThai());
        res.setNgayTao(entity.getNgayTao());
        return res;
    }
}
