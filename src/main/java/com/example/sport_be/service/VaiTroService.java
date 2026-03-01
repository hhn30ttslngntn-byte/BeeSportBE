package com.example.sport_be.service;

import com.example.sport_be.DTO.VaiTroDTO;
import com.example.sport_be.model.VaiTro;
import com.example.sport_be.repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VaiTroService {
    private final VaiTroRepository repository;

    public VaiTroDTO create(VaiTroDTO dto) {

        VaiTro entity = new VaiTro();
        entity.setTenVaiTro(dto.getTenVaiTro());
        entity.setTrangThai(true);

        return mapToDTO(repository.save(entity));
    }

    public List<VaiTroDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private VaiTroDTO mapToDTO(VaiTro entity) {
        VaiTroDTO dto = new VaiTroDTO();
        dto.setId(entity.getId());
        dto.setTenVaiTro(entity.getTenVaiTro());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }
}
