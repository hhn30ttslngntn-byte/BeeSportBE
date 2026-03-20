package com.example.sport_be.service.impl;

import com.example.sport_be.DTO.VaiTroDTO;
import com.example.sport_be.repository.RoleRepository;
import com.example.sport_be.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;

    @Override
    public List<VaiTroDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(role -> VaiTroDTO.builder()
                        .id(role.getId())
                        .tenVaiTro(role.getTenVaiTro())
                        .trangThai(role.getTrangThai())
                        .build())
                .toList();
    }
}
