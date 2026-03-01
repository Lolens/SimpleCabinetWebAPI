package com.gravitlauncher.simplecabinet.web.repository.update;

import com.gravitlauncher.simplecabinet.web.model.updates.Profile;
import com.gravitlauncher.simplecabinet.web.model.updates.UpdateDirectory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UpdateDirectoryRepository extends JpaRepository<UpdateDirectory, Long> {
    @Query("select ud from UpdateDirectory ud, UpdateProfile up, Profile p where ud = up.assets and up.profile = p and up.tag = p.tag and p = :profile")
    Optional<UpdateDirectory> findAssetsByProfile(Profile profile);

    @Query("select ud from UpdateDirectory ud where ud.unconnectedName = ?1 order by ud.updateAt desc limit 1")
    Optional<UpdateDirectory> findLatestByUnconnectedName(String name);

    @Query("select ud from UpdateDirectory ud, UpdateProfile up, Profile p where ud = up.client and up.profile = p and up.tag = p.tag and p = :profile")
    Optional<UpdateDirectory> findClientByProfile(Profile profile);
}
