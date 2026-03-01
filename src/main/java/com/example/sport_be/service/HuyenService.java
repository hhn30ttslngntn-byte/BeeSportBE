package com.example.sport_be.service;

import com.example.sport_be.DTO.HuyenDTO;
import com.example.sport_be.model.Huyen;
import com.example.sport_be.repository.HuyenRepository;
import com.example.sport_be.repository.TinhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HuyenService {
    private final HuyenRepository repository;
    private final TinhRepository tinhRepository;

    public HuyenDTO create(HuyenDTO dto) {

        Huyen entity = new Huyen();
        entity.setTenHuyen(dto.getTenHuyen());
        entity.setTrangThai(true);
        entity.setTinh(
                tinhRepository.findById(dto.getIdTinh())
                        .orElseThrow(() -> new RuntimeException("Tinh not found"))
        );

        return mapToDTO(repository.save(entity));
    }

    public List<HuyenDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private HuyenDTO mapToDTO(Huyen entity) {
        HuyenDTO dto = new HuyenDTO();
        dto.setId(entity.getId());
        dto.setTenHuyen(entity.getTenHuyen());
        dto.setTrangThai(entity.getTrangThai());
        dto.setIdTinh(entity.getTinh().getId());
        return dto;
    }
}
