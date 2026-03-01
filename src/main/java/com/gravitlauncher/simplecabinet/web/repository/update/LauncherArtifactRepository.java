package com.gravitlauncher.simplecabinet.web.repository.update;

import com.gravitlauncher.simplecabinet.web.model.updates.LauncherArtifact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LauncherArtifactRepository extends JpaRepository<LauncherArtifact, Long> {
    Optional<LauncherArtifact> findByPublicKey(byte[] publicKey);

    @Query("select la from LauncherArtifact la where la.artifactType = ?1 and la.deprecated = false order by la.uploadAt desc limit 1")
    Optional<LauncherArtifact> findLatestRelease(String variant);

    @Modifying
    @Query("update LauncherArtifact la set la.deprecated = true where la.artifactType = ?1 and la.id != ?2")
    void markAllOldArtifactIsDeprecated(String variant, Long latestReleaseId);
}
