package com.gravitlauncher.simplecabinet.web.repository.update;

import com.gravitlauncher.simplecabinet.web.model.updates.Profile;
import com.gravitlauncher.simplecabinet.web.model.updates.UpdateProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UpdateProfileRepository extends JpaRepository<UpdateProfile, Long> {
    Optional<UpdateProfile> findByProfileAndTag(Profile profile, String tag);

    @Query("select p from UpdateProfile p join fetch p.client join fetch p.assets where p.profile = ?1 and p.tag = ?2")
    Optional<UpdateProfile> findByProfileAndTagWithFetch(Profile profile, String tag);
}
