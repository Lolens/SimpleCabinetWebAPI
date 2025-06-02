package com.gravitlauncher.simplecabinet.web.repository.update;

import com.gravitlauncher.simplecabinet.web.model.updates.Profile;
import com.gravitlauncher.simplecabinet.web.model.updates.UpdateProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UpdateProfileRepository extends JpaRepository<UpdateProfile, Long> {
    Optional<UpdateProfile> findByProfileAndTag(Profile profile, String tag);
}
