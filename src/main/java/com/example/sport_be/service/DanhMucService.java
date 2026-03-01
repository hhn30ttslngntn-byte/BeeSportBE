package com.example.sport_be.service;

import com.example.sport_be.DTO.DanhMucRequest;
import com.example.sport_be.DTO.DanhMucResponse;
import com.example.sport_be.model.DanhMuc;
import com.example.sport_be.repository.DanhMucRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DanhMucService {
    private final DanhMucRepository repository;

    public DanhMucResponse create(DanhMucRequest request) {

        DanhMuc entity = new DanhMuc();
        entity.setTenDanhMuc(request.getTenDanhMuc());
        entity.setTrangThai(true);

        return mapToResponse(repository.save(entity));
    }

    public List<DanhMucResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private DanhMucResponse mapToResponse(DanhMuc entity) {
        DanhMucResponse res = new DanhMucResponse();
        res.setId(entity.getId());
        res.setTenDanhMuc(entity.getTenDanhMuc());
        res.setTrangThai(entity.getTrangThai());
        return res;
    }
}
