package com.gravitlauncher.simplecabinet.web.controller;

import com.gravitlauncher.simplecabinet.web.service.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.gravitlauncher.simplecabinet.web.dto.PageDto;
import com.gravitlauncher.simplecabinet.web.dto.user.HardwareInfoDto;
import com.gravitlauncher.simplecabinet.web.dto.user.UserDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.user.*;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private SerchUserService search;
    @Autowired
    private UserService service;
    @Autowired
    private UserGroupService groupService;
    @Autowired
    private UserAssetService userAssetService;
    @Autowired
    private HardwareIdService hardwareIdService;
    @Autowired
    private DtoService dtoService;

    @GetMapping("/id/{userId}")
    public UserDto getById(@PathVariable long userId, @RequestParam boolean assets) {
        var optional = assets ? service.findByIdFetchAssets(userId) : service.findById(userId);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        return dtoService.toPublicUserDto(optional.get());
    }

    @GetMapping("/id/{userId}/hardware")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<HardwareInfoDto> getHardwareByUserId(@PathVariable long userId) {
        var optional = service.findById(userId);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        return hardwareIdService.findByUser(optional.get().getId()).stream().map(HardwareInfoDto::new).toList();
    }

    @DeleteMapping("/id/{userId}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteStatus(@PathVariable long userId) {
        var optional = service.findById(userId);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        var user = optional.get();
        user.setStatus(null);
        service.save(user);
    }

    @DeleteMapping("/id/{userId}/asset/{assetName}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteStatus(@PathVariable long userId, @PathVariable String assetName) {
        var optional = userAssetService.findByUserAndName(service.getReference(userId), assetName.toLowerCase(Locale.ROOT));
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("User/Asset not found");
        }
        var asset = optional.get();
        userAssetService.delete(asset);
    }

    @DeleteMapping("/id/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteUserById(@PathVariable long userId) {
        var optional = service.findById(userId);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        service.delete(optional.get());
    }

    public record AddGroupRequest(long days, int priority) {

    }

    @GetMapping("/name/{name}")
    public UserDto getByUsername(@PathVariable String name, @RequestParam boolean assets) {
        var optional = assets ? service.findByUsernameFetchAssets(name) : service.findByUsername(name);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        return dtoService.toPublicUserDto(optional.get());
    }

    @GetMapping("/uuid/{uuid}")
    public UserDto getByUUID(@PathVariable UUID uuid, @RequestParam boolean assets) {
        var optional = assets ? service.findByUuidFetchAssets(uuid) : service.findByUUID(uuid);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        return dtoService.toPublicUserDto(optional.get());
    }

    @GetMapping("/search/{data}/{pageId}")
    public PageDto<UserDto> searchByData(@PathVariable String data, @PathVariable int pageId ) {
                var page = PageRequest.of(pageId, 10);
                var list = search.findByUsernameFetchAssets(data, page);
                return new PageDto<>(list.map(dtoService::toMiniUserDto));
    }

    @GetMapping("/search/email/{data}/{pageId}")
    public PageDto<UserDto> searchByEmail(@PathVariable String data,@PathVariable int pageId ) {
        var page = PageRequest.of(pageId, 10);
        var list = search.findByEmail(data, page);
        if (list.isEmpty()) {
            throw new EntityNotFoundException("User not found");
            }
        return new PageDto<>(list.map(dtoService::toMiniUserDto));
    }

    @GetMapping("/page/{pageId}")
    public PageDto<UserDto> getPage(@PathVariable int pageId) {
        var list = service.findAll(PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(dtoService::toMiniUserDto));
    }
}
