package com.gravitlauncher.simplecabinet.web.controller.launcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.model.updates.UpdateDirectory;
import com.gravitlauncher.simplecabinet.web.model.updates.UpdateProfile;
import com.gravitlauncher.simplecabinet.web.service.updates.ProfileService;
import com.gravitlauncher.simplecabinet.web.service.updates.UpdateDirectoryService;
import com.gravitlauncher.simplecabinet.web.service.updates.UpdateProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/launcher/profile")
public class LauncherProfilesController {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private UpdateProfileService updateProfileService;
    @Autowired
    private UpdateDirectoryService updateDirectoryService;

    @GetMapping("/list")
    public HttpListProfilesResponse getAll() {
        var profiles = profileService.findAllWithUpdateProfile();
        return new HttpListProfilesResponse(profiles.stream().map(UpdateProfile::getContent).collect(Collectors.toList()));
    }

    @GetMapping("/{uuid}/dir/{name}")
    public HttpUpdateInfo getUpdateDir(@PathVariable UUID uuid, @PathVariable String name) {
        Optional<UpdateDirectory> directory;
        if (name.equals("assets")) {
            directory = updateDirectoryService.findAssetsByProfile(profileService.getReferenceById(uuid));
        } else {
            directory = updateDirectoryService.findClientByProfile(profileService.getReferenceById(uuid));
        }
        if (directory.isEmpty()) {
            throw new EntityNotFoundException("Directory not found");
        }
        return new HttpUpdateInfo(directory.get().getContent(), "https://example.com");
    }

    public record HttpListProfilesResponse(List<JsonNode> profiles) {

    }

    public record HttpUpdateInfo(JsonNode dir, String baseUrl) {

    }
}
