package com.example.sport_be.service;

import com.example.sport_be.DTO.LichSuHoaDonDTO;
import com.example.sport_be.model.LichSuHoaDon;
import com.example.sport_be.repository.HoaDonRepository;
import com.example.sport_be.repository.LichSuHoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LichSuHoaDonService {
    private final LichSuHoaDonRepository repository;
    private final HoaDonRepository hoaDonRepository;

    public LichSuHoaDonDTO create(LichSuHoaDonDTO dto) {

        LichSuHoaDon entity = new LichSuHoaDon();
        entity.setTrangThai(dto.getTrangThai());
        entity.setNgayCapNhat(LocalDateTime.now());
        entity.setHoaDon(
                hoaDonRepository.findById(dto.getIdHoaDon())
                        .orElseThrow(() -> new RuntimeException("HoaDon not found"))
        );

        return mapToDTO(repository.save(entity));
    }

    private LichSuHoaDonDTO mapToDTO(LichSuHoaDon entity) {
        LichSuHoaDonDTO dto = new LichSuHoaDonDTO();
        dto.setId(entity.getId());
        dto.setIdHoaDon(entity.getHoaDon().getId());
        dto.setTrangThai(entity.getTrangThai());
        dto.setNgayCapNhat(entity.getNgayCapNhat());
        return dto;
    }
}
