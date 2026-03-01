package com.example.sport_be.service;

import com.example.sport_be.DTO.GioHangChiTietDTO;
import com.example.sport_be.model.GioHangChiTiet;
import com.example.sport_be.repository.GioHangChiTietRepository;
import com.example.sport_be.repository.GioHangRepository;
import com.example.sport_be.repository.SanPhamChiTietRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GioHangChiTietService {
    private final GioHangChiTietRepository repository;
    private final GioHangRepository gioHangRepository;
    private final SanPhamChiTietRepository spctRepository;

    public GioHangChiTietDTO create(GioHangChiTietDTO dto) {

        GioHangChiTiet entity = new GioHangChiTiet();
        entity.setSoLuong(dto.getSoLuong());
        entity.setGioHang(
                gioHangRepository.findById(dto.getIdGioHang())
                        .orElseThrow(() -> new RuntimeException("GioHang not found"))
        );
        entity.setSanPhamChiTiet(
                spctRepository.findById(dto.getIdSpct())
                        .orElseThrow(() -> new RuntimeException("SPCT not found"))
        );

        return mapToDTO(repository.save(entity));
    }

    private GioHangChiTietDTO mapToDTO(GioHangChiTiet entity) {
        GioHangChiTietDTO dto = new GioHangChiTietDTO();
        dto.setId(entity.getId());
        dto.setIdGioHang(entity.getGioHang().getId());
        dto.setIdSpct(entity.getSanPhamChiTiet().getId());
        dto.setSoLuong(entity.getSoLuong());
        return dto;
    }
}
