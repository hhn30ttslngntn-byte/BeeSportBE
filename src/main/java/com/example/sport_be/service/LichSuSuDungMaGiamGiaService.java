package com.example.sport_be.service;

import com.example.sport_be.DTO.LichSuSuDungMaGiamGiaDTO;
import com.example.sport_be.model.LichSuSuDungMaGiamGia;
import com.example.sport_be.repository.HoaDonRepository;
import com.example.sport_be.repository.LichSuSuDungMaGiamGiaRepository;
import com.example.sport_be.repository.MaGiamGiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LichSuSuDungMaGiamGiaService {
    private final LichSuSuDungMaGiamGiaRepository repository;
    private final MaGiamGiaRepository maRepository;
    private final HoaDonRepository hoaDonRepository;

    public LichSuSuDungMaGiamGiaDTO create(LichSuSuDungMaGiamGiaDTO dto) {

        LichSuSuDungMaGiamGia entity = new LichSuSuDungMaGiamGia();
        entity.setNgaySuDung(LocalDateTime.now());
        entity.setMaGiamGia(
                maRepository.findById(dto.getIdMaGiamGia())
                        .orElseThrow(() -> new RuntimeException("Ma not found"))
        );
        entity.setHoaDon(
                hoaDonRepository.findById(dto.getIdHoaDon())
                        .orElseThrow(() -> new RuntimeException("HoaDon not found"))
        );

        return mapToDTO(repository.save(entity));
    }

    private LichSuSuDungMaGiamGiaDTO mapToDTO(LichSuSuDungMaGiamGia entity) {
        LichSuSuDungMaGiamGiaDTO dto = new LichSuSuDungMaGiamGiaDTO();
        dto.setId(entity.getId());
        dto.setIdMaGiamGia(entity.getMaGiamGia().getId());
        dto.setIdHoaDon(entity.getHoaDon().getId());
        dto.setNgaySuDung(entity.getNgaySuDung());
        return dto;
    }
}
