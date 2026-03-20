package com.example.sport_be.service;

import com.example.sport_be.DTO.KichThuocDTO;
import com.example.sport_be.model.KichThuoc;

import java.util.List;
import java.util.Map;

public interface SizeService {

    List<KichThuocDTO> getAll();
    KichThuocDTO getById(Integer id);
    KichThuocDTO create(KichThuocDTO request);
    KichThuocDTO update(Integer id, KichThuocDTO request);
    void delete(Integer id);
}
