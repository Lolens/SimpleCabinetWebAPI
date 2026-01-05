package com.gravitlauncher.simplecabinet.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gravitlauncher.simplecabinet.web.controller.integration.BanManagerController;
import com.gravitlauncher.simplecabinet.web.dto.shop.GroupProductDto;
import com.gravitlauncher.simplecabinet.web.dto.shop.ItemDeliveryDto;
import com.gravitlauncher.simplecabinet.web.dto.shop.ItemProductDto;
import com.gravitlauncher.simplecabinet.web.dto.shop.ServiceProductDto;
import com.gravitlauncher.simplecabinet.web.dto.updates.ProfileDto;
import com.gravitlauncher.simplecabinet.web.dto.user.UserDto;
import com.gravitlauncher.simplecabinet.web.dto.user.UserGroupDto;
import com.gravitlauncher.simplecabinet.web.model.shop.GroupProduct;
import com.gravitlauncher.simplecabinet.web.model.shop.ItemDelivery;
import com.gravitlauncher.simplecabinet.web.model.shop.ItemProduct;
import com.gravitlauncher.simplecabinet.web.model.shop.ServiceProduct;
import com.gravitlauncher.simplecabinet.web.model.updates.Profile;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserAsset;
import com.gravitlauncher.simplecabinet.web.service.storage.StorageService;
import com.gravitlauncher.simplecabinet.web.service.user.UserDetailsService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DtoService {
    @Autowired
    private UserService userService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public GroupProductDto toGroupProductDto(GroupProduct entity) {
        return new GroupProductDto(entity.getId(), entity.getServer(), entity.getPrice(), entity.isStackable(), entity.getCurrency(), entity.getDisplayName(), entity.getDescription(),
                entity.getPictureUrl() != null ? storageService.getUrl(entity.getPictureUrl()).toString() : null, entity.getExpireDays(), entity.isAvailable());
    }

    public ItemProductDto toItemProductDto(ItemProduct entity) {
        return new ItemProductDto(entity.getId(), entity.getServer(), entity.getPrice(), entity.getCurrency(), entity.getDisplayName(), entity.getDescription(),
                entity.getPictureUrl() != null ? storageService.getUrl(entity.getPictureUrl()).toString() : null,
                entity.getLimitations());
    }

    public ServiceProductDto toServiceProductDto(ServiceProduct entity) {
        return new ServiceProductDto(entity.getId(), entity.getPrice(), entity.isStackable(), entity.getCurrency(), entity.getDisplayName(), entity.getDescription(),
                entity.getPictureUrl() != null ? storageService.getUrl(entity.getPictureUrl()).toString() : null,
                entity.getLimitations());
    }

    public ProfileDto toProfileDto(Profile profile) {
        return new ProfileDto(profile.getId(), profile.getName(), profile.getDescription(),
                storageService.getUrl(profile.getIconId()).toString(),
                storageService.getUrl(profile.getPictureId()).toString(),
                storageService.getUrl(profile.getLargePictureId()).toString(),
                profile.isLimited(),
                profile.getTag());
    }

    @Transactional
    public UserDto toPublicUserDto(User user) {
        var groups = userService.getUserGroups(user).stream()
                .map(UserGroupDto::new)
                .collect(Collectors.toList());

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getUuid().toString(),
                user.getGender() != null ?
                        UserDto.Gender.valueOf(user.getGender().name()) : null,
                user.getReputation() != null ? user.getReputation().intValue() : 0,
                user.getStatus(),
                user.getRegistrationDate(),
                groups,
                getUserTextures(user),
                null // Для публичного DTO разрешений нет
        );
    }

    @Transactional
    public BanManagerController.UserUUID toUsernameUuid(User user) {
        return new BanManagerController.UserUUID(user.getUsername(), user.getUuid());
    }

    @Transactional
    public UserDto toPrivateUserDto(User user) {
        var groups = userService.getUserGroups(user);
        var groupsDto = groups.stream()
                .map(UserGroupDto::new)
                .collect(Collectors.toList());

        // Получаем разрешения через UserDetailsService
        Map<String, String> permissions = userDetailsService.getUserPermissions(user);

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getUuid().toString(),
                user.getGender() != null ?
                        UserDto.Gender.valueOf(user.getGender().name()) : null,
                user.getReputation() != null ? user.getReputation().intValue() : 0,
                user.getStatus(),
                user.getRegistrationDate(),
                groupsDto,
                getUserTextures(user),
                permissions // Разрешения из UserDetailsService
        );
    }

    @Transactional
    public UserDto toMiniUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getUuid().toString(),
                user.getGender() != null ?
                        UserDto.Gender.valueOf(user.getGender().name()) : null,
                user.getReputation() != null ? user.getReputation().intValue() : 0,
                user.getStatus(),
                user.getRegistrationDate(),
                null,
                getUserTextures(user),
                null
        );
    }

    public ItemDeliveryDto itemDeliveryDto(ItemDelivery delivery) {
        List<ItemDeliveryDto.ItemEnchantDto> list;
        if (delivery.getItemEnchants() != null) {
            try {
                var type = new TypeReference<List<ItemDeliveryDto.ItemEnchantDto>>() {};
                list = new ObjectMapper().readValue(delivery.getItemEnchants(), type);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            list = null;
        }
        return new ItemDeliveryDto(delivery.getId(), delivery.getItemName(), delivery.getItemExtra(), list, delivery.getItemNbt(), delivery.getPart(), delivery.isCompleted());
    }

    private Map<String, String> deserializeMetadata(JsonNode metadata) {
        if (metadata == null || metadata.isNull() || metadata.isEmpty()) {
            return Map.of();
        }
        try {
            return objectMapper.convertValue(metadata, new TypeReference<Map<String, String>>() {});
        } catch (IllegalArgumentException e) {
            return Map.of();
        }
    }

    public Map<String, UserDto.UserTexture> getUserTextures(User user) {
        Map<String, UserDto.UserTexture> textures = new HashMap<>();

        if (user.getAssets() != null) {
            for (UserAsset asset : user.getAssets()) {
                textures.put(
                        asset.getType().name().toLowerCase(),
                        getUserTexture(asset)
                );
            }
        }

        return textures;
    }

    public UserDto.UserTexture getUserTexture(UserAsset asset) {
        return new UserDto.UserTexture(
                baseUrl + asset.getUrl(),
                asset.getDigest(), // SHA256
                deserializeMetadata(asset.getMetadata())
        );
    }

    /**
     * Конвертация UserAsset в DTO для нового API
     */
    public UserDto.TextureAssetDto toTextureAssetDto(UserAsset asset) {
        return new UserDto.TextureAssetDto(
                asset.getId(),
                asset.getType().name().toLowerCase(),
                baseUrl + asset.getUrl(),
                asset.getDigest(),
                deserializeMetadata(asset.getMetadata()),
                asset.getWidth(),
                asset.getHeight(),
                asset.getFileSize(),
                asset.getUploadedAt()
        );
    }

    public TextureDto toTextureDto(UserAsset asset) {
        return new TextureDto(
                asset.getId(),
                asset.getType().name().toLowerCase(),
                asset.getUrl(),
                asset.getDigest(),
                deserializeMetadata(asset.getMetadata()),
                asset.getWidth(),
                asset.getHeight(),
                asset.getFileSize(),
                asset.getUploadedAt()
        );
    }

    public record TextureDto(
            Long id,
            String type,
            String url,
            String digest,
            Map<String, String> metadata,
            Integer width,
            Integer height,
            Long fileSize,
            LocalDateTime uploadedAt
    ) {
        public TextureDto(Long id, String type, String url, String digest,
                          Map<String, String> metadata, Integer width,
                          Integer height, Long fileSize, LocalDateTime uploadedAt) {
            this.id = id;
            this.type = type;
            this.url = url;
            this.digest = digest;
            this.metadata = metadata != null ? metadata : new HashMap<>();
            this.width = width;
            this.height = height;
            this.fileSize = fileSize;
            this.uploadedAt = uploadedAt;
        }
    }
}