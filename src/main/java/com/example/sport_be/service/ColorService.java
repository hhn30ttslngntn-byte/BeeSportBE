package com.example.sport_be.service;

import com.example.sport_be.DTO.MauSacDTO;

import java.util.List;

public interface ColorService {
    List<MauSacDTO> getAll();
    MauSacDTO getById(Integer id);
    MauSacDTO create(MauSacDTO request);
    MauSacDTO update(Integer id, MauSacDTO request);
    void delete(Integer id);
}
