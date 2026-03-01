package com.example.sport_be.service;

import com.example.sport_be.DTO.LichSuThanhToanDTO;
import com.example.sport_be.model.LichSuThanhToan;
import com.example.sport_be.repository.HoaDonRepository;
import com.example.sport_be.repository.LichSuThanhToanRepository;
import com.example.sport_be.repository.PhuongThucThanhToanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LichSuThanhToanService {
    private final LichSuThanhToanRepository repository;
    private final HoaDonRepository hoaDonRepository;
    private final PhuongThucThanhToanRepository ptttRepository;

    public LichSuThanhToanDTO create(LichSuThanhToanDTO dto) {

        LichSuThanhToan entity = new LichSuThanhToan();
        entity.setSoTien(dto.getSoTien());
        entity.setTrangThaiThanhToan(dto.getTrangThaiThanhToan());
        entity.setNgayThanhToan(LocalDateTime.now());
        entity.setHoaDon(
                hoaDonRepository.findById(dto.getIdHoaDon())
                        .orElseThrow(() -> new RuntimeException("HoaDon not found"))
        );
        entity.setPhuongThucThanhToan(
                ptttRepository.findById(dto.getIdPttt())
                        .orElseThrow(() -> new RuntimeException("PTTT not found"))
        );

        return mapToDTO(repository.save(entity));
    }

    private LichSuThanhToanDTO mapToDTO(LichSuThanhToan entity) {
        LichSuThanhToanDTO dto = new LichSuThanhToanDTO();
        dto.setId(entity.getId());
        dto.setIdHoaDon(entity.getHoaDon().getId());
        dto.setIdPttt(entity.getPhuongThucThanhToan().getId());
        dto.setSoTien(entity.getSoTien());
        dto.setTrangThaiThanhToan(entity.getTrangThaiThanhToan());
        dto.setNgayThanhToan(entity.getNgayThanhToan());
        return dto;
    }
}
