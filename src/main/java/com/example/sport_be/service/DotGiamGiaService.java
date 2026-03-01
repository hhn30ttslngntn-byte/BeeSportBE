package com.example.sport_be.service;

import com.example.sport_be.DTO.DotGiamGiaDTO;
import com.example.sport_be.model.DotGiamGia;
import com.example.sport_be.repository.DotGiamGiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DotGiamGiaService {
    private final DotGiamGiaRepository repository;

    public DotGiamGiaDTO create(DotGiamGiaDTO dto) {

        DotGiamGia entity = new DotGiamGia();
        entity.setTenDot(dto.getTenDot());
        entity.setKieuGiamGia(dto.getKieuGiamGia());
        entity.setGiaTriGiam(dto.getGiaTriGiam());
        entity.setNgayBatDau(dto.getNgayBatDau());
        entity.setNgayKetThuc(dto.getNgayKetThuc());
        entity.setTrangThai(true);

        return mapToDTO(repository.save(entity));
    }

    private DotGiamGiaDTO mapToDTO(DotGiamGia entity) {
        DotGiamGiaDTO dto = new DotGiamGiaDTO();
        dto.setId(entity.getId());
        dto.setTenDot(entity.getTenDot());
        dto.setKieuGiamGia(entity.getKieuGiamGia());
        dto.setGiaTriGiam(entity.getGiaTriGiam());
        dto.setNgayBatDau(entity.getNgayBatDau());
        dto.setNgayKetThuc(entity.getNgayKetThuc());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }
}
