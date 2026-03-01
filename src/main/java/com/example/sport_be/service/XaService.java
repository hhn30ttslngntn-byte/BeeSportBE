package com.example.sport_be.service;

import com.example.sport_be.DTO.XaDTO;
import com.example.sport_be.model.Xa;
import com.example.sport_be.repository.HuyenRepository;
import com.example.sport_be.repository.XaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class XaService {
    private final XaRepository repository;
    private final HuyenRepository huyenRepository;

    public XaDTO create(XaDTO dto) {

        Xa entity = new Xa();
        entity.setTenXa(dto.getTenXa());
        entity.setTrangThai(true);
        entity.setHuyen(
                huyenRepository.findById(dto.getIdHuyen())
                        .orElseThrow(() -> new RuntimeException("Huyen not found"))
        );

        return mapToDTO(repository.save(entity));
    }

    private XaDTO mapToDTO(Xa entity) {
        XaDTO dto = new XaDTO();
        dto.setId(entity.getId());
        dto.setTenXa(entity.getTenXa());
        dto.setTrangThai(entity.getTrangThai());
        dto.setIdHuyen(entity.getHuyen().getId());
        return dto;
    }
}
