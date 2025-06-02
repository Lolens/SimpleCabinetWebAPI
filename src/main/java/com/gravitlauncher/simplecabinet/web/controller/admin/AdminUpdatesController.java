package com.gravitlauncher.simplecabinet.web.controller.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.gravitlauncher.simplecabinet.web.dto.updates.UpdateDirectoryDto;
import com.gravitlauncher.simplecabinet.web.dto.updates.UpdateProfileDto;
import com.gravitlauncher.simplecabinet.web.service.updates.ProfileService;
import com.gravitlauncher.simplecabinet.web.service.updates.UpdateDirectoryService;
import com.gravitlauncher.simplecabinet.web.service.updates.UpdateProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/admin/updates")
public class AdminUpdatesController {
    @Autowired
    private UpdateDirectoryService updateDirectoryService;
    @Autowired
    private UpdateProfileService updateProfileService;
    @Autowired
    private ProfileService profileService;

    @PostMapping("/pushdirectory")
    public UpdateDirectoryDto pushUpdateDirectory(@RequestBody JsonNode node) {
        return new UpdateDirectoryDto(updateDirectoryService.create(node));
    }

    @PostMapping("/pushprofile")
    public UpdateProfileDto pushUpdateProfile(@RequestBody PushUpdateProfile pushUpdateProfile) {
        var profile = profileService.getReferenceById(pushUpdateProfile.profileUuid());
        var client = updateDirectoryService.getReferenceById(pushUpdateProfile.clientId());
        var assets = updateDirectoryService.getReferenceById(pushUpdateProfile.assetId());
        var previous = pushUpdateProfile.previous() != null ? updateProfileService.getReferenceById(pushUpdateProfile.previous()) : null;
        return new UpdateProfileDto(updateProfileService.create(profile, client, assets, pushUpdateProfile.content(), pushUpdateProfile.tag(), previous));
    }

    public record PushUpdateProfile(UUID profileUuid, Long clientId, Long assetId, JsonNode content, String tag, Long previous) {

    }
}
