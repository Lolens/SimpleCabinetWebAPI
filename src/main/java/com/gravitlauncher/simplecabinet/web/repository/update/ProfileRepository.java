package com.gravitlauncher.simplecabinet.web.repository.update;

import com.gravitlauncher.simplecabinet.web.model.updates.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    @Modifying
    @Query("update Profile p set p.tag = :tag where p.id = :uuid")
    void updateProfileTag(UUID uuid, String tag);
}
