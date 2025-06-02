package com.gravitlauncher.simplecabinet.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gravitlauncher.simplecabinet.web.controller.integration.BanManagerController;
import com.gravitlauncher.simplecabinet.web.dto.shop.GroupProductDto;
import com.gravitlauncher.simplecabinet.web.dto.shop.ItemDeliveryDto;
import com.gravitlauncher.simplecabinet.web.dto.shop.ItemProductDto;
import com.gravitlauncher.simplecabinet.web.dto.shop.ServiceProductDto;
import com.gravitlauncher.simplecabinet.web.dto.user.UserDto;
import com.gravitlauncher.simplecabinet.web.dto.user.UserGroupDto;
import com.gravitlauncher.simplecabinet.web.model.shop.GroupProduct;
import com.gravitlauncher.simplecabinet.web.model.shop.ItemDelivery;
import com.gravitlauncher.simplecabinet.web.model.shop.ItemProduct;
import com.gravitlauncher.simplecabinet.web.model.shop.ServiceProduct;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserAsset;
import com.gravitlauncher.simplecabinet.web.service.storage.StorageService;
import com.gravitlauncher.simplecabinet.web.service.user.UserAssetService;
import com.gravitlauncher.simplecabinet.web.service.user.UserDetailsService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DtoService {
    @Autowired
    private UserService userService;
    @Autowired
    private UserAssetService userAssetService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ObjectMapper objectMapper;

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

    @Transactional
    public UserDto toPublicUserDto(User user) {
        var groups = userService.getUserGroups(user).stream().map(UserGroupDto::new).collect(Collectors.toList());
        return new UserDto(user.getId(), user.getUsername(), user.getUuid(), user.getGender(), user.getReputation(), user.getStatus(), user.getRegistrationDate(),
                groups, getUserTextures(user), null);
    }

    @Transactional
    public BanManagerController.UserUUID toUsernameUuid(User user) {
        return new BanManagerController.UserUUID(user.getUsername(),user.getUuid());
    }

    @Transactional
    public UserDto toPrivateUserDto(User user) {
        var groups = userService.getUserGroups(user);
        var groupsDto = groups.stream().map(UserGroupDto::new).collect(Collectors.toList());
        return new UserDto(user.getId(), user.getUsername(), user.getUuid(), user.getGender(), user.getReputation(), user.getStatus(), user.getRegistrationDate(),
                groupsDto, getUserTextures(user), userDetailsService.getUserPermissions(user));
    }

    public UserDto toMiniUserDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getUuid(), user.getGender(), user.getReputation(), user.getStatus(), user.getRegistrationDate(),
                null, getUserTextures(user), null);
    }

    public ItemDeliveryDto itemDeliveryDto(ItemDelivery delivery) {
        List<ItemDeliveryDto.ItemEnchantDto> list;
        if (delivery.getItemEnchants() != null) {
            try {
                var type = new TypeReference<List<ItemDeliveryDto.ItemEnchantDto>>() {
                };
                list = new ObjectMapper().readValue(delivery.getItemEnchants(), type);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            list = null;
        }
        return new ItemDeliveryDto(delivery.getId(), delivery.getItemName(), delivery.getItemExtra(), list, delivery.getItemNbt(), delivery.getPart(), delivery.isCompleted());
    }

    private Map<String, String> deserializeMetadata(String metadata) {
        try {
            TypeReference<Map<String, String>> type = new TypeReference<>() {
            };
            return objectMapper.readValue(metadata, type);
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }

    public Map<String, UserDto.UserTexture> getUserTextures(User user) {
        return user.getAssets().stream().collect(Collectors.toMap(UserAsset::getName,
                this::getUserTexture));
    }

    public UserDto.UserTexture getUserTexture(UserAsset asset) {
        return new UserDto.UserTexture(userAssetService.makeAssetUrl(asset), asset.getHash(), deserializeMetadata(asset.getMetadata()));
    }
}
