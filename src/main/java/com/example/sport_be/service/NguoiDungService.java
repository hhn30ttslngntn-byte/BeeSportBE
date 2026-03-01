package com.example.sport_be.service;

import com.example.sport_be.DTO.NguoiDungRequest;
import com.example.sport_be.DTO.NguoiDungResponse;
import com.example.sport_be.model.NguoiDung;
import com.example.sport_be.model.VaiTro;
import com.example.sport_be.repository.NguoiDungRepository;
import com.example.sport_be.repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NguoiDungService {
    private final NguoiDungRepository repository;
    private final VaiTroRepository vaiTroRepository;

    public NguoiDungResponse create(NguoiDungRequest request) {

        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        VaiTro vaiTro = vaiTroRepository.findById(request.getIdVaiTro())
                .orElseThrow(() -> new RuntimeException("VaiTro not found"));

        NguoiDung entity = new NguoiDung();
        entity.setHoTen(request.getHoTen());
        entity.setEmail(request.getEmail());
        entity.setMatKhau(request.getMatKhau());
        entity.setTrangThai(true);
        entity.setVaiTro(vaiTro);

        return mapToResponse(repository.save(entity));
    }

    public List<NguoiDungResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private NguoiDungResponse mapToResponse(NguoiDung entity) {
        NguoiDungResponse res = new NguoiDungResponse();
        res.setId(entity.getId());
        res.setHoTen(entity.getHoTen());
        res.setEmail(entity.getEmail());
        res.setTrangThai(entity.getTrangThai());
        res.setIdVaiTro(entity.getVaiTro().getId());
        return res;
    }
}
