package com.gravitlauncher.simplecabinet.web.service.updates;

import com.gravitlauncher.simplecabinet.web.model.updates.Profile;
import com.gravitlauncher.simplecabinet.web.repository.update.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository repository;

    public Profile create(String name, String description) {
        Profile profile = new Profile();
        profile.setName(name);
        profile.setDescription(description);
        return save(profile);
    }

    public void updateProfileTag(UUID uuid, String tag) {
        repository.updateProfileTag(uuid, tag);
    }

    public List<Profile> findAll() {
        return repository.findAll();
    }

    public <S extends Profile> S save(S entity) {
        return repository.save(entity);
    }

    public Optional<Profile> findById(UUID uuid) {
        return repository.findById(uuid);
    }

    public Profile getReferenceById(UUID uuid) {
        return repository.getReferenceById(uuid);
    }
}
