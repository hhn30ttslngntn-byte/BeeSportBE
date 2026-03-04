package com.example.sport_be.service;

import com.example.sport_be.DTO.DiaChiVanChuyenDTO;
import com.example.sport_be.model.DiaChiVanChuyen;
import com.example.sport_be.model.NguoiDung;
import com.example.sport_be.model.Xa;
import com.example.sport_be.repository.DiaChiVanChuyenRepository;
import com.example.sport_be.repository.NguoiDungRepository;
import com.example.sport_be.repository.XaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaChiVanChuyenService {

    private final DiaChiVanChuyenRepository repository;
    private final NguoiDungRepository nguoiDungRepository;
    private final XaRepository xaRepository;

    public DiaChiVanChuyenDTO create(DiaChiVanChuyenDTO dto) {

        NguoiDung nguoiDung = nguoiDungRepository.findById(dto.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("NguoiDung not found"));

        Xa xa = xaRepository.findById(dto.getIdXa())
                .orElseThrow(() -> new RuntimeException("Xa not found"));

        DiaChiVanChuyen entity = new DiaChiVanChuyen();
        entity.setMaDiaChiVanChuyen(dto.getMaDiaChiVanChuyen());
        entity.setNguoiDung(nguoiDung);
        entity.setXa(xa);
        entity.setDiaChiChiTiet(dto.getDiaChiChiTiet());
        entity.setTrangThai(true);

        return mapToDTO(repository.save(entity));
    }

    public List<DiaChiVanChuyenDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DiaChiVanChuyenDTO getById(Integer id) {
        DiaChiVanChuyen entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DiaChiVanChuyen not found"));
        return mapToDTO(entity);
    }

    public List<DiaChiVanChuyenDTO> getByUser(Integer idNguoiDung) {
        return repository.findByNguoiDung_Id(idNguoiDung)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DiaChiVanChuyenDTO update(Integer id, DiaChiVanChuyenDTO dto) {

        DiaChiVanChuyen entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DiaChiVanChuyen not found"));

        NguoiDung nguoiDung = nguoiDungRepository.findById(dto.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("NguoiDung not found"));

        Xa xa = xaRepository.findById(dto.getIdXa())
                .orElseThrow(() -> new RuntimeException("Xa not found"));

        entity.setMaDiaChiVanChuyen(dto.getMaDiaChiVanChuyen());
        entity.setNguoiDung(nguoiDung);
        entity.setXa(xa);
        entity.setDiaChiChiTiet(dto.getDiaChiChiTiet());
        entity.setTrangThai(dto.getTrangThai());

        return mapToDTO(repository.save(entity));
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    private DiaChiVanChuyenDTO mapToDTO(DiaChiVanChuyen entity) {
        DiaChiVanChuyenDTO dto = new DiaChiVanChuyenDTO();
        dto.setId(entity.getId());
        dto.setMaDiaChiVanChuyen(entity.getMaDiaChiVanChuyen());
        dto.setIdNguoiDung(entity.getNguoiDung().getId());
        dto.setIdXa(entity.getXa().getId());
        dto.setDiaChiChiTiet(entity.getDiaChiChiTiet());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }
}