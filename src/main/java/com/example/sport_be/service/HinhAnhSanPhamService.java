package com.example.sport_be.service;

import com.example.sport_be.DTO.HinhAnhSanPhamDTO;
import com.example.sport_be.model.HinhAnhSanPham;
import com.example.sport_be.repository.HinhAnhSanPhamRepository;
import com.example.sport_be.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HinhAnhSanPhamService {
    private final HinhAnhSanPhamRepository repository;
    private final SanPhamRepository sanPhamRepository;

    public HinhAnhSanPhamDTO create(HinhAnhSanPhamDTO dto) {

        HinhAnhSanPham entity = new HinhAnhSanPham();
        entity.setUrl(dto.getUrl());
        entity.setTrangThai(true);
        entity.setSanPham(
                sanPhamRepository.findById(dto.getIdSanPham())
                        .orElseThrow(() -> new RuntimeException("SanPham not found"))
        );

        return mapToDTO(repository.save(entity));
    }

    private HinhAnhSanPhamDTO mapToDTO(HinhAnhSanPham entity) {
        HinhAnhSanPhamDTO dto = new HinhAnhSanPhamDTO();
        dto.setId(entity.getId());
        dto.setUrl(entity.getUrl());
        dto.setTrangThai(entity.getTrangThai());
        dto.setIdSanPham(entity.getSanPham().getId());
        return dto;
    }
}
