package com.gravitlauncher.simplecabinet.web.dto.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record UserDto(
        Long id,
        String username,
        String uuid,
        Gender gender,
        Integer reputation,
        String status,
        LocalDateTime registrationDate,
        List<UserGroupDto> groups,
        Map<String, UserTexture> textures,
        Map<String, String> permissions
) {

    // Существующий UserTexture для обратной совместимости
    public record UserTexture(
            String url,
            String hash,
            Map<String, String> metadata
    ) {}

    // Новый DTO для текстур с полной информацией
    public record TextureAssetDto(
            Long id,
            String type, // "skin", "cape", "avatar"
            String url,
            String digest, // SHA256
            Map<String, String> metadata,
            Integer width,
            Integer height,
            Long fileSize,
            LocalDateTime uploadedAt
    ) {}

    public enum Gender {
        MALE,
        FEMALE
    }
}