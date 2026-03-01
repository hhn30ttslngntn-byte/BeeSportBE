package com.example.sport_be.service;

import com.example.sport_be.DTO.PhuongThucThanhToanDTO;
import com.example.sport_be.model.PhuongThucThanhToan;
import com.example.sport_be.repository.PhuongThucThanhToanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhuongThucThanhToanService {
    private final PhuongThucThanhToanRepository repository;

    public PhuongThucThanhToanDTO create(PhuongThucThanhToanDTO dto) {

        PhuongThucThanhToan entity = new PhuongThucThanhToan();
        entity.setTenPttt(dto.getTenPttt());
        entity.setTrangThai(true);

        return mapToDTO(repository.save(entity));
    }

    private PhuongThucThanhToanDTO mapToDTO(PhuongThucThanhToan entity) {
        PhuongThucThanhToanDTO dto = new PhuongThucThanhToanDTO();
        dto.setId(entity.getId());
        dto.setTenPttt(entity.getTenPttt());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }
}
