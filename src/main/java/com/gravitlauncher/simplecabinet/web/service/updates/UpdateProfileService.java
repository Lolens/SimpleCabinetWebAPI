package com.gravitlauncher.simplecabinet.web.service.updates;

import com.fasterxml.jackson.databind.JsonNode;
import com.gravitlauncher.simplecabinet.web.model.updates.Profile;
import com.gravitlauncher.simplecabinet.web.model.updates.UpdateDirectory;
import com.gravitlauncher.simplecabinet.web.model.updates.UpdateProfile;
import com.gravitlauncher.simplecabinet.web.repository.update.UpdateProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UpdateProfileService {
    @Autowired
    private UpdateProfileRepository repository;

    public UpdateProfile create(Profile profile, UpdateDirectory client, UpdateDirectory assets, JsonNode content, String tag, UpdateProfile previous) {
        UpdateProfile updateProfile = new UpdateProfile();
        updateProfile.setProfile(profile);
        updateProfile.setCreatedAt(LocalDateTime.now());
        updateProfile.setClient(client);
        updateProfile.setAssets(assets);
        updateProfile.setContent(content);
        updateProfile.setTag(tag);
        updateProfile.setPrevious(previous);
        return save(updateProfile);
    }

    public UpdateProfile getReferenceById(Long aLong) {
        return repository.getReferenceById(aLong);
    }

    public Optional<UpdateProfile> findById(Long aLong) {
        return repository.findById(aLong);
    }

    public <S extends UpdateProfile> S save(S entity) {
        return repository.save(entity);
    }

    public Optional<UpdateProfile> findByProfileAndTag(Profile profile, String tag) {
        return repository.findByProfileAndTag(profile, tag);
    }
}
