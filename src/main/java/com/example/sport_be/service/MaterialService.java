package com.example.sport_be.service;

import com.example.sport_be.DTO.ChatLieuDTO;

import java.util.List;

public interface MaterialService {
    List<ChatLieuDTO> getAll();
    ChatLieuDTO getById(Integer id);
    ChatLieuDTO create(ChatLieuDTO request);
    ChatLieuDTO update(Integer id, ChatLieuDTO request);
    void delete(Integer id);
}
