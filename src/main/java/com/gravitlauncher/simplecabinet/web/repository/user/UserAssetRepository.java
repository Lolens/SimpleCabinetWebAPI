package com.gravitlauncher.simplecabinet.web.repository.user;

import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAssetRepository extends JpaRepository<UserAsset, Long> {

    Optional<UserAsset> findByUserAndType(User user, UserAsset.AssetType type);

    Optional<UserAsset> findByDigest(String digest);

    List<UserAsset> findByUser(User user);

    long countByDigest(String digest);

    boolean existsByUserAndType(User user, UserAsset.AssetType type);

    List<UserAsset> findAllByUser(User user);

    Optional<UserAsset> findByUserAndFileName(User user, String name);

    @Query("SELECT DISTINCT ua.type, ua.digest FROM UserAsset ua")
    List<Object[]> findAllUsedFileCombinations();

    @Query("SELECT ua FROM UserAsset ua WHERE ua.digest = :digest ORDER BY ua.uploadedAt ASC")
    List<UserAsset> findAllByDigest(@Param("digest") String digest);
}