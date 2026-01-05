package com.gravitlauncher.simplecabinet.web.controller;

import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.model.user.TextureFileInfoDto;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserAsset;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.DtoService.TextureDto;
import com.gravitlauncher.simplecabinet.web.service.TextureService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/textures")
public class TextureController {

    private final TextureService textureService;
    private final UserService userService;
    private final DtoService dtoService;

    @GetMapping("/{username}")
    public TextureResponseDto getTextures(@PathVariable String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidParametersException("Username cannot be empty", 400);
        }

        User user = userService.findByUsername(username.trim())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return textureService.getTexturesForLauncher(user);
    }

    @GetMapping("/uuid/{uuid}")
    public TextureResponseDto getTexturesByUuid(@PathVariable String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new InvalidParametersException("UUID cannot be empty", 400);
        }

        try {
            UUID userUuid = UUID.fromString(uuid.trim());
            User user = userService.findByUUID(userUuid)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            return textureService.getTexturesForLauncher(user);
        } catch (IllegalArgumentException e) {
            throw new InvalidParametersException("Invalid UUID format", 400);
        }
    }

    @GetMapping("/info/{digest}")
    public TextureFileInfoDto getFileInfo(@PathVariable String digest) {
        return textureService.getFileUsageInfo(digest);
    }

    @GetMapping("/file/{digest}")
    public ResponseEntity<byte[]> getTextureFile(@PathVariable String digest) {
        if (digest == null || digest.trim().isEmpty()) {
            throw new InvalidParametersException("Digest cannot be empty", 400);
        }

        byte[] fileData = textureService.getTextureFile(digest.trim())
                .orElseThrow(() -> new EntityNotFoundException("Texture file not found"));

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .header("Cache-Control", "public, max-age=86400")
                .body(fileData);
    }

    // --- Для веб-интерфейса и API ---

    @GetMapping("/user/{userId}")
    public List<TextureDto> getUserTextures(@PathVariable String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidParametersException("User ID cannot be empty", 400);
        }

        try {
            Long id = Long.parseLong(userId.trim());
            User user = userService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            return textureService.getUserTextures(user).stream()
                    .map(dtoService::toTextureDto)
                    .toList();
        } catch (NumberFormatException e) {
            throw new InvalidParametersException("Invalid user ID format", 400);
        }
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public List<TextureDto> getMyTextures() {
        User user = userService.getCurrentUser().getReference();
        return textureService.getUserTextures(user).stream()
                .map(dtoService::toTextureDto)
                .toList();
    }

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public UploadTextureResponseDto uploadTexture(
            @RequestParam String type,
            @RequestParam MultipartFile file,
            @RequestParam(required = false, defaultValue = "false") boolean slim) {

        if (type == null || type.trim().isEmpty()) {
            throw new InvalidParametersException("Texture type cannot be empty", 400);
        }

        if (file == null || file.isEmpty()) {
            throw new InvalidParametersException("File cannot be empty", 400);
        }

        User user = userService.getCurrentUser().getReference();
        UserAsset.AssetType assetType = UserAsset.AssetType.fromString(type.trim().toLowerCase());

        if (assetType == null) {
            throw new InvalidParametersException("Invalid texture type. Use: skin, cape", 400);
        }

        UserAsset asset = textureService.uploadTexture(
                user,
                assetType,
                file,
                slim && assetType == UserAsset.AssetType.SKIN ?
                        java.util.Map.of("model", "slim") : java.util.Map.of()
        );

        return new UploadTextureResponseDto(
                asset.getId(),
                asset.getType().name().toLowerCase(),
                asset.getUrl(),
                asset.getDigest(),
                asset.getWidth(),
                asset.getHeight(),
                asset.getFileSize(),
                asset.getMetadata()
        );
    }

    @DeleteMapping("/{type}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTexture(@PathVariable String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new InvalidParametersException("Texture type cannot be empty", 400);
        }

        User user = userService.getCurrentUser().getReference();
        UserAsset.AssetType assetType = UserAsset.AssetType.fromString(type.trim().toLowerCase());

        if (assetType == null) {
            throw new InvalidParametersException("Invalid texture type", 400);
        }

        textureService.deleteTexture(user, assetType);
    }

    @GetMapping("/check/{digest}")
    public CheckTextureResponseDto checkTexture(@PathVariable String digest) {
        if (digest == null || digest.trim().isEmpty()) {
            throw new InvalidParametersException("Digest cannot be empty", 400);
        }

        boolean exists = textureService.getTextureFile(digest.trim()).isPresent();
        return new CheckTextureResponseDto(exists, digest.trim());
    }

    // --- Вспомогательные DTO классы ---

    public static class TextureResponseDto {
        private final TextureDto skin;
        private final TextureDto cape;

        public TextureResponseDto(TextureDto skin, TextureDto cape) {
            this.skin = skin;
            this.cape = cape;
        }

        public TextureDto getSkin() {
            return skin;
        }

        public TextureDto getCape() {
            return cape;
        }
    }

    public static class UploadTextureResponseDto {
        private final Long id;
        private final String type;
        private final String url;
        private final String digest;
        private final Integer width;
        private final Integer height;
        private final Long fileSize;
        private final Object metadata;

        public UploadTextureResponseDto(Long id, String type, String url, String digest,
                                        Integer width, Integer height, Long fileSize, Object metadata) {
            this.id = id;
            this.type = type;
            this.url = url;
            this.digest = digest;
            this.width = width;
            this.height = height;
            this.fileSize = fileSize;
            this.metadata = metadata;
        }

        // Getters
        public Long getId() { return id; }
        public String getType() { return type; }
        public String getUrl() { return url; }
        public String getDigest() { return digest; }
        public Integer getWidth() { return width; }
        public Integer getHeight() { return height; }
        public Long getFileSize() { return fileSize; }
        public Object getMetadata() { return metadata; }
    }

    public static class CheckTextureResponseDto {
        private final boolean exists;
        private final String digest;

        public CheckTextureResponseDto(boolean exists, String digest) {
            this.exists = exists;
            this.digest = digest;
        }

        public boolean isExists() {
            return exists;
        }

        public String getDigest() {
            return digest;
        }
    }
}