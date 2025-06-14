package com.gravitlauncher.simplecabinet.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.gravitlauncher.simplecabinet.web.dto.updates.ProfileDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.updates.ProfileService;
import com.gravitlauncher.simplecabinet.web.service.updates.UpdateDirectoryService;
import com.gravitlauncher.simplecabinet.web.service.updates.UpdateProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/profile")
public class ProfilesController {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private UpdateProfileService updateProfileService;
    @Autowired
    private UpdateDirectoryService updateDirectoryService;
    @Autowired
    private DtoService dtoService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/new")
    public ProfileDto createProfile(@RequestBody ProfileCreateRequest request) {
        var profile = profileService.create(request.name, request.description);
        return dtoService.toProfileDto(profile);
    }

    @GetMapping("/list")
    public List<ProfileDto> profilesList() {
        // TODO implement limited profiles
        var list = profileService.findAll();
        return list.stream().map(dtoService::toProfileDto).toList();
    }

    @GetMapping("/by/id/{uuid}")
    public ProfileDto getByUUID(@PathVariable UUID uuid) {
        var profile = profileService.findById(uuid);
        if (profile.isEmpty()) {
            throw new EntityNotFoundException("Profile not found");
        }
        return dtoService.toProfileDto(profile.get());
    }

    @GetMapping("/by/id/{uuid}/profile")
    public JsonNode getProfileByUUID(@PathVariable UUID uuid, @RequestParam(defaultValue = "default") String tag) {
        var profile = profileService.findById(uuid);
        if (profile.isEmpty()) {
            throw new EntityNotFoundException("Profile not found");
        }
        if (tag.equals("default")) {
            tag = profile.get().getTag();
        }
        var updateProfile = updateProfileService.findByProfileAndTag(profile.get(), tag);
        if (updateProfile.isEmpty()) {
            throw new EntityNotFoundException("Profile is empty");
        }
        return updateProfile.get().getContent();
    }

    @GetMapping("/by/id/{uuid}/updates")
    public ProfileUpdateResponse getUpdatesByUUID(@PathVariable UUID uuid, @RequestParam(defaultValue = "default") String tag) {
        var profile = profileService.findById(uuid);
        if (profile.isEmpty()) {
            throw new EntityNotFoundException("Profile not found");
        }
        if (tag.equals("default")) {
            tag = profile.get().getTag();
        }
        var updateProfile = updateProfileService.findByProfileAndTagWithFetch(profile.get(), tag);
        if (updateProfile.isEmpty()) {
            throw new EntityNotFoundException("Profile is empty");
        }
        return new ProfileUpdateResponse(updateProfile.get().getClient().getContent(), updateProfile.get().getAssets().getContent(),
                updateProfile.get().getContent(),
                updateProfile.get().getTag());
    }

    public record ProfileCreateRequest(String name, String description) {

    }

    public record ProfileUpdateResponse(JsonNode client, JsonNode assets, JsonNode content, String tag) {

    }
}
