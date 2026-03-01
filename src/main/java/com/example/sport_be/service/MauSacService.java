package com.example.sport_be.service;

import com.example.sport_be.DTO.MauSacDTO;
import com.example.sport_be.model.MauSac;
import com.example.sport_be.repository.MauSacRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MauSacService {
    private final MauSacRepository repository;

    public MauSacDTO create(MauSacDTO dto) {

        MauSac entity = new MauSac();
        entity.setTenMau(dto.getTenMau());
        entity.setTrangThai(true);

        return mapToDTO(repository.save(entity));
    }

    public List<MauSacDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private MauSacDTO mapToDTO(MauSac entity) {
        MauSacDTO dto = new MauSacDTO();
        dto.setId(entity.getId());
        dto.setTenMau(entity.getTenMau());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }
}
