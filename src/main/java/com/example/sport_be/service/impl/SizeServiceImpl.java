package com.example.sport_be.service.impl;

import com.example.sport_be.DTO.KichThuocDTO;
import com.example.sport_be.model.KichThuoc;
import com.example.sport_be.repository.SizeRepository;
import com.example.sport_be.service.SizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

    private final SizeRepository repository;

    @Override
    public List<KichThuocDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public KichThuocDTO getById(Integer id) {
        KichThuoc entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Size not found"));
        return toDTO(entity);
    }

    @Override
    public KichThuocDTO create(KichThuocDTO request) {

        if (repository.existsByTenKichThuoc(request.getTenKichThuoc())) {
            throw new IllegalArgumentException("Size name already exists");
        }

        KichThuoc entity = KichThuoc.builder()
                .tenKichThuoc(request.getTenKichThuoc())
                .trangThai(request.getTrangThai() != null ? request.getTrangThai() : true)
                .build();

        return toDTO(repository.save(entity));
    }

    @Override
    public KichThuocDTO update(Integer id, KichThuocDTO request) {

        KichThuoc entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Size not found"));

        if (!entity.getTenKichThuoc().equals(request.getTenKichThuoc())
                && repository.existsByTenKichThuoc(request.getTenKichThuoc())) {
            throw new IllegalArgumentException("Size name already exists");
        }

        entity.setTenKichThuoc(request.getTenKichThuoc());

        if (request.getTrangThai() != null) {
            entity.setTrangThai(request.getTrangThai());
        }

        return toDTO(repository.save(entity));
    }

    @Override
    public void delete(Integer id) {

        KichThuoc entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Size not found"));

        entity.setTrangThai(false); // soft delete
        repository.save(entity);
    }

    private KichThuocDTO toDTO(KichThuoc entity) {
        return KichThuocDTO.builder()
                .id(entity.getId())
                .tenKichThuoc(entity.getTenKichThuoc())
                .trangThai(entity.getTrangThai())
                .build();
    }
}
