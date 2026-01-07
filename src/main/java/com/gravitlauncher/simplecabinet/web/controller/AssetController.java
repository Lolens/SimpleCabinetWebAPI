package com.gravitlauncher.simplecabinet.web.controller;

import com.gravitlauncher.simplecabinet.web.configuration.properties.storage.FileStorageConfig;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.model.user.TextureFileInfoDto;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserAsset;
import com.gravitlauncher.simplecabinet.web.service.AssetService;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.DtoService.AssetDto;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assets")
public class AssetController {

    private final AssetService assetService;
    private final UserService userService;
    private final DtoService dtoService;
    private final FileStorageConfig storageConfig;

    @GetMapping("/{username}")
    public AssetResponseDto getAssets(@PathVariable String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidParametersException("Username cannot be empty", 400);
        }

        User user = userService.findByUsername(username.trim())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return assetService.getAssetsForLauncher(user);
    }

    @GetMapping("/uuid/{uuid}")
    public AssetResponseDto getAssetsByUuid(@PathVariable String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new InvalidParametersException("UUID cannot be empty", 400);
        }

        try {
            UUID userUuid = UUID.fromString(uuid.trim());
            User user = userService.findByUUID(userUuid)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            return assetService.getAssetsForLauncher(user);
        } catch (IllegalArgumentException e) {
            throw new InvalidParametersException("Invalid UUID format", 400);
        }
    }

    @GetMapping("/info/{digest}")
    public TextureFileInfoDto getFileInfo(@PathVariable String digest) {
        return assetService.getFileUsageInfo(digest);
    }

    @GetMapping("/file/{digest}")
    public ResponseEntity<byte[]> getAssetFile(@PathVariable String digest) {
        if (digest == null || digest.trim().isEmpty()) {
            throw new InvalidParametersException("Digest cannot be empty", 400);
        }

        byte[] fileData = assetService.getTextureFile(digest.trim())
                .orElseThrow(() -> new EntityNotFoundException("Texture file not found"));

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .header("Cache-Control", "public, max-age=86400")
                .body(fileData);
    }

    @GetMapping("/user/{userId}")
    public List<AssetDto> getUserAssets(@PathVariable String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidParametersException("User ID cannot be empty", 400);
        }

        try {
            Long id = Long.parseLong(userId.trim());
            User user = userService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            return assetService.getUserAssets(user).stream()
                    .map(dtoService::toTextureDto)
                    .toList();
        } catch (NumberFormatException e) {
            throw new InvalidParametersException("Invalid user ID format", 400);
        }
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public List<AssetDto> getMyAssets() {
        User user = userService.getCurrentUser().getReference();
        return assetService.getUserAssets(user).stream()
                .map(dtoService::toTextureDto)
                .toList();
    }

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public UploadAssetResponseDto uploadTexture(
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

        UserAsset asset = assetService.uploadTexture(
                user,
                assetType,
                file,
                slim && assetType == UserAsset.AssetType.SKIN ?
                        java.util.Map.of("model", "slim") : java.util.Map.of()
        );

        return new UploadAssetResponseDto(
                asset.getId(),
                asset.getType().name().toLowerCase(),
                storageConfig.getRemoteUrl() + asset.getUrl(),
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

        assetService.deleteTexture(user, assetType);
    }

    @GetMapping("/check/{digest}")
    public CheckAssetResponseDto checkAsset(@PathVariable String digest) {
        if (digest == null || digest.trim().isEmpty()) {
            throw new InvalidParametersException("Digest cannot be empty", 400);
        }

        boolean exists = assetService.getTextureFile(digest.trim()).isPresent();
        return new CheckAssetResponseDto(exists, digest.trim());
    }

    // --- Вспомогательные DTO классы ---


    public record AssetResponseDto(DtoService.AssetDto skin, DtoService.AssetDto cape) {
    }



    public record UploadAssetResponseDto(Long id, String type, String url, String digest, Integer width,
                                         Integer height, Long fileSize, Object metadata) {

    }


        public record CheckAssetResponseDto(boolean exists, String digest) {

    }
}