package com.example.sport_be.service;

import com.example.sport_be.DTO.MaGiamGiaDTO;
import com.example.sport_be.model.MaGiamGia;
import com.example.sport_be.repository.MaGiamGiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MaGiamGiaService {
    private final MaGiamGiaRepository repository;

    public MaGiamGiaDTO create(MaGiamGiaDTO dto) {

        MaGiamGia entity = new MaGiamGia();
        entity.setMaCode(dto.getMaCode());
        entity.setKieuGiamGia(dto.getKieuGiamGia());
        entity.setGiaTriGiam(dto.getGiaTriGiam());
        entity.setSoLuong(dto.getSoLuong());
        entity.setNgayBatDau(dto.getNgayBatDau());
        entity.setNgayKetThuc(dto.getNgayKetThuc());
        entity.setTrangThai(true);

        return mapToDTO(repository.save(entity));
    }

    private MaGiamGiaDTO mapToDTO(MaGiamGia entity) {
        MaGiamGiaDTO dto = new MaGiamGiaDTO();
        dto.setId(entity.getId());
        dto.setMaCode(entity.getMaCode());
        dto.setKieuGiamGia(entity.getKieuGiamGia());
        dto.setGiaTriGiam(entity.getGiaTriGiam());
        dto.setSoLuong(entity.getSoLuong());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }
}
