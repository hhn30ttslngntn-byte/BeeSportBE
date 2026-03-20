package com.example.sport_be.controller;

import com.example.sport_be.DTO.VaiTroDTO;
import com.example.sport_be.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public List<VaiTroDTO> getAll() {
        return roleService.getAll();
    }
}
