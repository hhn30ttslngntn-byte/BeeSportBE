package com.example.sport_be.service.impl;

import com.example.sport_be.DTO.ChatLieuDTO;
import com.example.sport_be.model.ChatLieu;
import com.example.sport_be.repository.MaterialRepository;
import com.example.sport_be.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository repository;

    @Override
    public List<ChatLieuDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public ChatLieuDTO getById(Integer id) {
        ChatLieu entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));
        return toDTO(entity);
    }

    @Override
    public ChatLieuDTO create(ChatLieuDTO request) {

        if (repository.existsByTenChatLieu(request.getTenChatLieu())) {
            throw new IllegalArgumentException("Material name already exists");
        }

        ChatLieu entity = ChatLieu.builder()
                .tenChatLieu(request.getTenChatLieu())
                .trangThai(request.getTrangThai() != null ? request.getTrangThai() : true)
                .build();

        return toDTO(repository.save(entity));
    }

    @Override
    public ChatLieuDTO update(Integer id, ChatLieuDTO request) {

        ChatLieu entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));

        if (!entity.getTenChatLieu().equals(request.getTenChatLieu())
                && repository.existsByTenChatLieu(request.getTenChatLieu())) {
            throw new IllegalArgumentException("Material name already exists");
        }

        entity.setTenChatLieu(request.getTenChatLieu());

        if (request.getTrangThai() != null) {
            entity.setTrangThai(request.getTrangThai());
        }

        return toDTO(repository.save(entity));
    }

    @Override
    public void delete(Integer id) {

        ChatLieu entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));

        entity.setTrangThai(false); // soft delete
        repository.save(entity);
    }

    private ChatLieuDTO toDTO(ChatLieu entity) {
        return ChatLieuDTO.builder()
                .id(entity.getId())
                .tenChatLieu(entity.getTenChatLieu())
                .trangThai(entity.getTrangThai())
                .build();
    }
}
