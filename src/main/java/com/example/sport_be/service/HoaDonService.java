package com.example.sport_be.service;

import com.example.sport_be.DTO.HoaDonRequest;
import com.example.sport_be.DTO.HoaDonResponse;
import com.example.sport_be.model.HoaDon;
import com.example.sport_be.model.NguoiDung;
import com.example.sport_be.repository.HoaDonRepository;
import com.example.sport_be.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HoaDonService {
    private final HoaDonRepository repository;
    private final NguoiDungRepository nguoiDungRepository;

    public HoaDonResponse create(HoaDonRequest request) {

        NguoiDung user = nguoiDungRepository.findById(request.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("User not found"));

        HoaDon entity = new HoaDon();
        entity.setNguoiDung(user);
        entity.setTrangThaiDon("CHO_XAC_NHAN");
        entity.setNgayTao(LocalDateTime.now());
        entity.setTongTien(BigDecimal.ZERO);

        return mapToResponse(repository.save(entity));
    }

    private HoaDonResponse mapToResponse(HoaDon entity) {
        HoaDonResponse res = new HoaDonResponse();
        res.setId(entity.getId());
        res.setIdNguoiDung(entity.getNguoiDung().getId());
        res.setTongTien(entity.getTongTien());
        res.setTrangThaiDon(entity.getTrangThaiDon());
        res.setNgayTao(entity.getNgayTao());
        return res;
    }
}
