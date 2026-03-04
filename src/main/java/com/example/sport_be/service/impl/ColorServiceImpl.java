package com.example.sport_be.service.impl;

import com.example.sport_be.DTO.MauSacDTO;
import com.example.sport_be.model.MauSac;
import com.example.sport_be.repository.ColorRepository;
import com.example.sport_be.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {

    private final ColorRepository repository;

    @Override
    public List<MauSacDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public MauSacDTO getById(Integer id) {
        MauSac entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Color not found"));
        return toDTO(entity);
    }

    @Override
    public MauSacDTO create(MauSacDTO request) {

        if (repository.existsByTenMau(request.getTenMau())) {
            throw new IllegalArgumentException("Color name already exists");
        }

        MauSac entity = MauSac.builder()
                .tenMau(request.getTenMau())
                .trangThai(request.getTrangThai() != null ? request.getTrangThai() : true)
                .build();

        return toDTO(repository.save(entity));
    }

    @Override
    public MauSacDTO update(Integer id, MauSacDTO request) {

        MauSac entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Color not found"));

        if (!entity.getTenMau().equals(request.getTenMau())
                && repository.existsByTenMau(request.getTenMau())) {
            throw new IllegalArgumentException("Color name already exists");
        }

        entity.setTenMau(request.getTenMau());

        if (request.getTrangThai() != null) {
            entity.setTrangThai(request.getTrangThai());
        }

        return toDTO(repository.save(entity));
    }

    @Override
    public void delete(Integer id) {

        MauSac entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Color not found"));

        entity.setTrangThai(false); // soft delete
        repository.save(entity);
    }

    private MauSacDTO toDTO(MauSac entity) {
        return MauSacDTO.builder()
                .id(entity.getId())
                .tenMau(entity.getTenMau())
                .trangThai(entity.getTrangThai())
                .build();
    }
}
