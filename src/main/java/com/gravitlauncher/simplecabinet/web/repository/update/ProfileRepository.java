package com.gravitlauncher.simplecabinet.web.repository.update;

import com.gravitlauncher.simplecabinet.web.model.updates.Profile;
import com.gravitlauncher.simplecabinet.web.model.updates.UpdateProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    @Modifying
    @Query("update Profile p set p.tag = :tag where p.id = :uuid")
    void updateProfileTag(UUID uuid, String tag);

    @Query("select up from UpdateProfile up, Profile p where up.profile = p and up.tag = p.tag and p.deleted = false")
    List<UpdateProfile> findAllWithUpdateProfile();

    @Modifying
    @Query("update Profile p set p.deleted = :value where p.id = :uuid")
    void updateDeleted(UUID uuid, boolean value);
}
