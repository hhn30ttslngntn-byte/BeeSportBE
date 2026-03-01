package com.example.sport_be.service;

import com.example.sport_be.DTO.TinhDTO;
import com.example.sport_be.model.Tinh;
import com.example.sport_be.repository.TinhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TinhService {
    private final TinhRepository repository;

    public TinhDTO create(TinhDTO dto) {

        Tinh entity = new Tinh();
        entity.setTenTinh(dto.getTenTinh());
        entity.setTrangThai(true);

        return mapToDTO(repository.save(entity));
    }

    private TinhDTO mapToDTO(Tinh entity) {
        TinhDTO dto = new TinhDTO();
        dto.setId(entity.getId());
        dto.setTenTinh(entity.getTenTinh());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }
}
