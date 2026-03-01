package com.example.sport_be.service;

import com.example.sport_be.DTO.ChatLieuDTO;
import com.example.sport_be.model.ChatLieu;
import com.example.sport_be.repository.ChatLieuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatLieuService {
    private final ChatLieuRepository repository;

    public ChatLieuDTO create(ChatLieuDTO dto) {

        ChatLieu entity = new ChatLieu();
        entity.setTenChatLieu(dto.getTenChatLieu());
        entity.setTrangThai(true);

        return mapToDTO(repository.save(entity));
    }

    private ChatLieuDTO mapToDTO(ChatLieu entity) {
        ChatLieuDTO dto = new ChatLieuDTO();
        dto.setId(entity.getId());
        dto.setTenChatLieu(entity.getTenChatLieu());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }
}
