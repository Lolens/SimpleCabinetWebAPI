package com.gravitlauncher.simplecabinet.web.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserAsset;

import java.util.List;
import java.util.Optional;

public interface UserAssetRepository extends JpaRepository<UserAsset, Long> {
    List<UserAsset> findAllByUser(User user);

    Optional<UserAsset> findByUserAndName(User user, String name);
}
