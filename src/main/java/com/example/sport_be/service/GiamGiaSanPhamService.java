package com.example.sport_be.service;

import com.example.sport_be.DTO.GiamGiaSanPhamDTO;
import com.example.sport_be.model.GiamGiaSanPham;
import com.example.sport_be.repository.DotGiamGiaRepository;
import com.example.sport_be.repository.GiamGiaSanPhamRepository;
import com.example.sport_be.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GiamGiaSanPhamService {
    private final GiamGiaSanPhamRepository repository;
    private final DotGiamGiaRepository dotRepository;
    private final SanPhamRepository sanPhamRepository;

    public GiamGiaSanPhamDTO create(GiamGiaSanPhamDTO dto) {

        GiamGiaSanPham entity = new GiamGiaSanPham();
        entity.setTrangThai(true);
        entity.setDotGiamGia(
                dotRepository.findById(dto.getIdDotGiamGia())
                        .orElseThrow(() -> new RuntimeException("Dot not found"))
        );
        entity.setSanPham(
                sanPhamRepository.findById(dto.getIdSanPham())
                        .orElseThrow(() -> new RuntimeException("SanPham not found"))
        );

        return mapToDTO(repository.save(entity));
    }

    private GiamGiaSanPhamDTO mapToDTO(GiamGiaSanPham entity) {
        GiamGiaSanPhamDTO dto = new GiamGiaSanPhamDTO();
        dto.setId(entity.getId());
        dto.setIdDotGiamGia(entity.getDotGiamGia().getId());
        dto.setIdSanPham(entity.getSanPham().getId());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }
}
