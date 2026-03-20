package com.example.sport_be.controller;

import com.example.sport_be.DTO.NguoiDungRequest;
import com.example.sport_be.DTO.NguoiDungResponse;
import com.example.sport_be.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<NguoiDungResponse> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public NguoiDungResponse getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @PostMapping
    public NguoiDungResponse create(@Valid @RequestBody NguoiDungRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    public NguoiDungResponse update(@PathVariable Integer id,
                                    @Valid @RequestBody NguoiDungRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }
}
