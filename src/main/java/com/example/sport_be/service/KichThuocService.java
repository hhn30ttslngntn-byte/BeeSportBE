package com.example.sport_be.service;

import com.example.sport_be.DTO.KichThuocDTO;
import com.example.sport_be.model.KichThuoc;
import com.example.sport_be.repository.KichThuocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KichThuocService {
    private final KichThuocRepository repository;

    public KichThuocDTO create(KichThuocDTO dto) {

        KichThuoc entity = new KichThuoc();
        entity.setTenKichThuoc(dto.getTenKichThuoc());
        entity.setTrangThai(true);

        return mapToDTO(repository.save(entity));
    }

    private KichThuocDTO mapToDTO(KichThuoc entity) {
        KichThuocDTO dto = new KichThuocDTO();
        dto.setId(entity.getId());
        dto.setTenKichThuoc(entity.getTenKichThuoc());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }
}
