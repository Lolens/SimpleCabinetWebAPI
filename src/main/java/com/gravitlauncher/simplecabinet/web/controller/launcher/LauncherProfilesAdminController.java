package com.gravitlauncher.simplecabinet.web.controller.launcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.gravitlauncher.simplecabinet.web.controller.cabinet.CabinetController;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.model.updates.Profile;
import com.gravitlauncher.simplecabinet.web.model.updates.UpdateDirectory;
import com.gravitlauncher.simplecabinet.web.model.updates.UpdateProfile;
import com.gravitlauncher.simplecabinet.web.service.storage.StorageService;
import com.gravitlauncher.simplecabinet.web.service.updates.ProfileService;
import com.gravitlauncher.simplecabinet.web.service.updates.UpdateDirectoryService;
import com.gravitlauncher.simplecabinet.web.service.updates.UpdateProfileService;
import com.gravitlauncher.simplecabinet.web.service.user.UserAssetService;
import com.gravitlauncher.simplecabinet.web.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/launcher/profile")
public class LauncherProfilesAdminController {
    private static final Logger logger = LoggerFactory.getLogger(CabinetController.class);
    @Autowired
    public UserAssetService userAssetService;
    @Autowired
    public StorageService storageService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private UpdateProfileService updateProfileService;
    @Autowired
    private UpdateDirectoryService updateDirectoryService;

    @DeleteMapping("/by/uuid/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteProfileByUuid(@PathVariable UUID uuid) {
        var profile = updateProfileService.findByProfileUuid(uuid);
        if (profile.isEmpty()) {
            throw new EntityNotFoundException("Profile not found");
        }
        profileService.delete(profile.get().getProfile());
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public HttpUncompletedProfile createProfile(@RequestBody HttpCreateProfile request) {
        Profile profile = new Profile();
        profile.setName(request.name);
        profile.setDescription(request.description);
        profile.setTag(generateNextTag(null));
        profileService.save(profile);
        UpdateProfile updateProfile = new UpdateProfile();
        updateProfile.setProfile(profile);
        updateProfile.setTag(profile.getTag());
        updateProfile.setContent(request.profile);
        updateProfileService.save(updateProfile);
        return new HttpUncompletedProfile(request.profile);
    }

    @PostMapping("/by/uuid/{uuid}/pushupdate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public HttpCompletedProfile pushUpdateProfile(@PathVariable UUID uuid, @RequestBody HttpPushUpdateProfile request) {
        var profileOptional = updateProfileService.findByProfileUuid(uuid);
        if (profileOptional.isEmpty()) {
            throw new EntityNotFoundException("Profile not found");
        }
        var oldUpdateProfile = profileOptional.get();
        var profile = oldUpdateProfile.getProfile();
        UpdateDirectory client;
        UpdateDirectory asset;
        if (request.assets != null) {
            asset = new UpdateDirectory();
            asset.setContent(request.assets);
            asset.setUpdateAt(LocalDateTime.now());
            asset.setUnconnectedName("assets");
            asset = updateDirectoryService.save(asset);
        } else {
            asset = oldUpdateProfile.getAssets();
        }
        if (request.client != null) {
            client = new UpdateDirectory();
            client.setContent(request.client);
            client.setUpdateAt(LocalDateTime.now());
            client = updateDirectoryService.save(client);
        } else {
            client = oldUpdateProfile.getClient();
        }
        UpdateProfile updateProfile = new UpdateProfile();
        updateProfile.setClient(client);
        updateProfile.setAssets(asset);
        updateProfile.setProfile(profile);
        updateProfile.setTag(generateNextTag(oldUpdateProfile.getTag()));
        updateProfile.setContent(request.profile);
        updateProfile = updateProfileService.save(updateProfile);
        profileService.updateProfileTag(profile.getId(), updateProfile.getTag());
        return new HttpCompletedProfile(request.profile, client.getContent(), asset.getContent());
    }


    @PostMapping("/uploadfile")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public HttpUploadedFile uploadFile(@RequestBody byte[] bytes) {
        String hash = userAssetService.calculateHash(bytes);
        try {
            URL url = storageService.put(hash, bytes);
            return new HttpUploadedFile(url.toString());
        } catch (StorageService.StorageException e) {
            logger.error("StorageService.put failed", e);
            throw new InvalidParametersException("File upload failure", 22);
        }
    }

    public String generateNextTag(String oldTag) {
        return SecurityUtils.generateRandomString(8);
    }

    public record HttpUploadedFile(String url) {

    }

    public record HttpCreateProfile(String name, String description, JsonNode profile) {

    }

    public record HttpUncompletedProfile(JsonNode profile) {

    }

    public record HttpCompletedProfile(JsonNode profile, JsonNode client, JsonNode assets) {

    }

    public record HttpPushUpdateProfile(JsonNode profile, JsonNode client, JsonNode assets) {

    }
}
