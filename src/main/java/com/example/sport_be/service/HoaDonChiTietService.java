package com.example.sport_be.service;

import com.example.sport_be.DTO.HoaDonChiTietDTO;
import com.example.sport_be.model.HoaDonChiTiet;
import com.example.sport_be.repository.HoaDonChiTietRepository;
import com.example.sport_be.repository.HoaDonRepository;
import com.example.sport_be.repository.SanPhamChiTietRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HoaDonChiTietService {
    private final HoaDonChiTietRepository repository;
    private final HoaDonRepository hoaDonRepository;
    private final SanPhamChiTietRepository spctRepository;

    public HoaDonChiTietDTO create(HoaDonChiTietDTO dto) {

        HoaDonChiTiet entity = new HoaDonChiTiet();
        entity.setSoLuong(dto.getSoLuong());
        entity.setGia(dto.getGia());

        entity.setHoaDon(
                hoaDonRepository.findById(dto.getIdHoaDon())
                        .orElseThrow(() -> new RuntimeException("HoaDon not found"))
        );

        entity.setSanPhamChiTiet(
                spctRepository.findById(dto.getIdSpct())
                        .orElseThrow(() -> new RuntimeException("SPCT not found"))
        );

        return mapToDTO(repository.save(entity));
    }

    private HoaDonChiTietDTO mapToDTO(HoaDonChiTiet entity) {
        HoaDonChiTietDTO dto = new HoaDonChiTietDTO();
        dto.setId(entity.getId());
        dto.setIdHoaDon(entity.getHoaDon().getId());
        dto.setIdSpct(entity.getSanPhamChiTiet().getId());
        dto.setSoLuong(entity.getSoLuong());
        dto.setGia(entity.getGia());
        return dto;
    }
}
