// ФАЙЛ: A:\Modding\Server\SCBackend\src\main\java\com\gravitlauncher\simplecabinet\web\service\TextureService.java

package com.gravitlauncher.simplecabinet.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gravitlauncher.simplecabinet.web.controller.TextureController;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.model.user.TextureFileInfoDto;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserAsset;
import com.gravitlauncher.simplecabinet.web.repository.user.UserAssetRepository;
import com.gravitlauncher.simplecabinet.web.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextureService {

    private final DtoService dtoService;
    private final UserAssetRepository userAssetRepository;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;


    @Transactional
    public UserAsset uploadTexture(User user, UserAsset.AssetType type,
                                   MultipartFile file, Map<String, String> metadata) {
        try {
            // 1. Валидация файла
            validateFile(file, type);
            byte[] fileData = file.getBytes();

            // 2. Валидация изображения
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileData));
            if (image == null) {
                throw new InvalidParametersException("Invalid image file", 23);
            }
            validateImageDimensions(image, type);

            // 3. Расчет хэша
            String digest = calculateSHA256(fileData);

            // 4. Получение существующих записей
            Optional<UserAsset> existingUserAsset = userAssetRepository
                    .findByUserAndType(user, type);

            // 5. Создание/получение объекта asset
            UserAsset asset;
            boolean isNewAsset = false;

            if (existingUserAsset.isPresent()) {
                asset = existingUserAsset.get();

                if (asset.getDigest().equals(digest)) {
                    asset.setMetadata(convertMetadataToJsonNode(metadata));
                    return userAssetRepository.save(asset);
                }

                String oldDigest = asset.getDigest();

                long usageCount = userAssetRepository.countByDigest(oldDigest) - 1;
                if (usageCount == 0) {
                    deleteFileFromStorage(oldDigest, type);
                }


            } else {
                asset = new UserAsset();
                asset.setUser(user);
                asset.setType(type);
                isNewAsset = true;
            }

            String storagePath = type.getFolder() + "/" + digest + ".png";
            try {
                storageService.put(storagePath, fileData);
                log.info("Saved new file: {}", storagePath);
            } catch (StorageService.StorageException e) {
                log.debug("File already exists in storage: {}", storagePath);
            }

            asset.setDigest(digest);
            asset.setFileName(digest + ".png");
            asset.setFileSize(file.getSize());
            asset.setWidth(image.getWidth());
            asset.setHeight(image.getHeight());
            asset.setMetadata(convertMetadataToJsonNode(metadata));

            if (isNewAsset) {
                asset.setUploadedAt(LocalDateTime.now());
            }

            return userAssetRepository.save(asset);

        } catch (IOException e) {
            log.error("Failed to process texture file", e);
            throw new InvalidParametersException("Failed to process file", 24);
        }
    }

    private boolean isFileStillUsed(String digest, User currentUser, UserAsset.AssetType currentType) {
        List<UserAsset> allAssetsWithDigest = userAssetRepository.findAllByDigest(digest);
        for (UserAsset asset : allAssetsWithDigest) {

            if (asset.getUser().getId() == currentUser.getId() &&
                    asset.getType() == currentType) {
                continue;
            }

            return true;
        }

        return false;
    }

    private void deleteFileFromStorage(String digest, UserAsset.AssetType type) {
        String storagePath = type.getFolder() + "/" + digest + ".png";
        try {
            storageService.delete(storagePath);
            log.info("Deleted unused file from storage: {}", storagePath);
        } catch (StorageService.StorageException e) {
            log.error("Failed to delete file: {}", storagePath, e);
        }
    }

    @Transactional
    public void deleteTexture(User user, UserAsset.AssetType type) {
        Optional<UserAsset> assetOpt = userAssetRepository.findByUserAndType(user, type);

        if (assetOpt.isPresent()) {
            UserAsset asset = assetOpt.get();
            String digest = asset.getDigest();

            userAssetRepository.delete(asset);
            boolean isStillUsed = isFileStillUsed(digest, user, type);

            if (!isStillUsed) {
                deleteFileFromStorage(digest, type);
            }

            log.info("User {} deleted {} texture (digest: {}, still used: {})",
                    user.getUsername(), type, digest, isStillUsed);
        }
    }

    public TextureFileInfoDto getFileUsageInfo(String digest) {
        List<UserAsset> allAssets = userAssetRepository.findAllByDigest(digest);

        Map<User, List<UserAsset.AssetType>> userUsage = new HashMap<>();
        for (UserAsset asset : allAssets) {
            userUsage.computeIfAbsent(asset.getUser(), k -> new ArrayList<>())
                    .add(asset.getType());
        }

        List<TextureFileInfoDto.UserUsage> userUsageList = userUsage.entrySet().stream()
                .map(entry -> new TextureFileInfoDto.UserUsage(
                        entry.getKey().getId(),
                        entry.getKey().getUsername(),
                        entry.getValue().stream()
                                .map(Enum::name)
                                .toList()
                ))
                .toList();

        return new TextureFileInfoDto(
                digest,
                allAssets.size(),
                userUsage.size(),
                userUsageList
        );
    }

    @Transactional
    public Map<String, Object> cleanupUnusedFiles() {
        List<Object[]> usedFiles = userAssetRepository.findAllUsedFileCombinations();
        Set<String> usedFilePaths = new HashSet<>();

        for (Object[] combo : usedFiles) {
            UserAsset.AssetType type = (UserAsset.AssetType) combo[0];
            String digest = (String) combo[1];
            usedFilePaths.add(type.getFolder() + "/" + digest + ".png");
        }

        List<String> allFilesInStorage;
        try {
            allFilesInStorage = storageService.listAllFiles();
        } catch (StorageService.StorageException e) {
            log.error("Failed to list storage files", e);
            return Map.of("status", "error", "message", "Failed to list storage files");
        }

        List<String> unusedFiles = allFilesInStorage.stream()
                .filter(file -> !usedFilePaths.contains(file))
                .toList();

        // Удаляем неиспользуемые файлы
        int deletedCount = 0;
        for (String filePath : unusedFiles) {
            try {
                storageService.delete(filePath);
                deletedCount++;
                log.info("Cleaned up unused file: {}", filePath);
            } catch (StorageService.StorageException e) {
                log.error("Failed to delete unused file: {}", filePath, e);
            }
        }

        return Map.of(
                "status", "success",
                "deletedFiles", deletedCount,
                "totalUnused", unusedFiles.size(),
                "totalUsed", usedFilePaths.size()
        );
    }

    // --- Вспомогательные методы ---

    private void validateFile(MultipartFile file, UserAsset.AssetType type) {
        // Проверка MIME типа
        if (!Objects.equals(file.getContentType(), "image/png")) {
            throw new InvalidParametersException("Only PNG files are allowed", 25);
        }

        // Проверка размера файла
        long maxSize = switch (type) {
            case SKIN -> 2 * 1024 * 1024; // 2MB
            case CAPE -> 1 * 1024 * 1024; // 1MB
            default -> 512 * 1024;    // 512KB для других типов
        };

        if (file.getSize() > maxSize) {
            throw new InvalidParametersException(
                    String.format("File size exceeds maximum allowed (%d bytes)", maxSize), 26);
        }
    }

    private void validateImageDimensions(BufferedImage image, UserAsset.AssetType type) {
        switch (type) {
            case SKIN -> validateSkinDimensions(image);
            case CAPE -> validateCapeDimensions(image);
            // AVATAR можно любых размеров
        }
    }

    private void validateSkinDimensions(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Допустимые размеры скинов
        boolean isValid = (width == 64 && height == 32) ||  // Старый формат
                (width == 64 && height == 64) ||   // Современный
                (width == 128 && height == 64) ||  // HD
                (width == 128 && height == 128) || // HD квадратный
                (width == 256 && height == 128) || // 2x HD
                (width == 256 && height == 256) || // 2x HD квадратный
                (width == 512 && height == 256) || // 4x HD
                (width == 512 && height == 512) || // 4x HD квадратный
                (width == 1024 && height == 512) ||// 8x HD
                (width == 1024 && height == 1024); // 8x HD квадратный

        if (!isValid) {
            throw new InvalidParametersException(
                    String.format("Invalid skin dimensions: %dx%d", width, height), 27);
        }
    }

    private void validateCapeDimensions(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Допустимые размеры плащей
        boolean isValid = (width == 64 && height == 32) ||   // Стандартный
                (width == 128 && height == 64) ||   // HD
                (width == 256 && height == 128) ||  // 2x HD
                (width == 512 && height == 256) ||  // 4x HD
                (width == 1024 && height == 512);   // 8x HD

        if (!isValid) {
            throw new InvalidParametersException(
                    String.format("Invalid cape dimensions: %dx%d", width, height), 28);
        }
    }

    private String calculateSHA256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private JsonNode convertMetadataToJsonNode(Map<String, String> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return objectMapper.createObjectNode();
        }
        return objectMapper.valueToTree(metadata);
    }

    public TextureController.TextureResponseDto getTexturesForLauncher(User user) {
        Optional<UserAsset> skinAsset = userAssetRepository.findByUserAndType(user, UserAsset.AssetType.SKIN);
        Optional<UserAsset> capeAsset = userAssetRepository.findByUserAndType(user, UserAsset.AssetType.CAPE);

        return new TextureController.TextureResponseDto(
                skinAsset.map(dtoService::toTextureDto).orElse(null),
                capeAsset.map(dtoService::toTextureDto).orElse(null)
        );
    }

    public Optional<byte[]> getTextureFile(String digest) {
        Optional<UserAsset> assetOpt = userAssetRepository.findByDigest(digest);
        if (assetOpt.isEmpty()) {
            return Optional.empty();
        }

        UserAsset asset = assetOpt.get();
        try {
            return storageService.get(asset.getStoragePath());
        } catch (Exception e) {
            log.error("Failed to get texture file", e);
            return Optional.empty();
        }
    }

    public List<UserAsset> getUserTextures(User user) {
        return userAssetRepository.findByUser(user);
    }
}