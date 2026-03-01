package com.example.sport_be.service;

import com.example.sport_be.DTO.DoiTraDTO;
import com.example.sport_be.model.DoiTra;
import com.example.sport_be.repository.DoiTraRepository;
import com.example.sport_be.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DoiTraService {
    private final DoiTraRepository repository;
    private final HoaDonRepository hoaDonRepository;

    public DoiTraDTO create(DoiTraDTO dto) {

        DoiTra entity = new DoiTra();
        entity.setLyDo(dto.getLyDo());
        entity.setTrangThai("CHO_DUYET");
        entity.setNgayYeuCau(LocalDateTime.now());
        entity.setHoaDon(
                hoaDonRepository.findById(dto.getIdHoaDon())
                        .orElseThrow(() -> new RuntimeException("HoaDon not found"))
        );

        return mapToDTO(repository.save(entity));
    }

    private DoiTraDTO mapToDTO(DoiTra entity) {
        DoiTraDTO dto = new DoiTraDTO();
        dto.setId(entity.getId());
        dto.setIdHoaDon(entity.getHoaDon().getId());
        dto.setLyDo(entity.getLyDo());
        dto.setTrangThai(entity.getTrangThai());
        dto.setNgayYeuCau(entity.getNgayYeuCau());
        return dto;
    }
}
