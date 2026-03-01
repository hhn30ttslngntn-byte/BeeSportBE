package com.example.sport_be.service;

import com.example.sport_be.DTO.SanPhamRequest;
import com.example.sport_be.DTO.SanPhamResponse;
import com.example.sport_be.model.DanhMuc;
import com.example.sport_be.model.SanPham;
import com.example.sport_be.repository.DanhMucRepository;
import com.example.sport_be.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SanPhamService {
    private final SanPhamRepository repository;
    private final DanhMucRepository danhMucRepository;

    public SanPhamResponse create(SanPhamRequest request) {

        DanhMuc danhMuc = danhMucRepository.findById(request.getIdDanhMuc())
                .orElseThrow(() -> new RuntimeException("DanhMuc not found"));

        SanPham entity = new SanPham();
        entity.setTenSanPham(request.getTenSanPham());
        entity.setGiaGoc(request.getGiaGoc());
        entity.setTrangThai(true);
        entity.setDanhMuc(danhMuc);

        return mapToResponse(repository.save(entity));
    }

    public List<SanPhamResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SanPhamResponse getById(Integer id) {
        return mapToResponse(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SanPham not found")));
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    private SanPhamResponse mapToResponse(SanPham entity) {
        SanPhamResponse res = new SanPhamResponse();
        res.setId(entity.getId());
        res.setTenSanPham(entity.getTenSanPham());
        res.setGiaGoc(entity.getGiaGoc());
        res.setTrangThai(entity.getTrangThai());
        res.setIdDanhMuc(entity.getDanhMuc().getId());
        return res;
    }
}
