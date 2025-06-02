package com.gravitlauncher.simplecabinet.web.repository.user;

import com.gravitlauncher.simplecabinet.web.model.user.PrepareUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PrepareUserRepository extends JpaRepository<PrepareUser, Long> {
    @Query("select u from PrepareUser u where u.username = :usernameOrEmail or u.email = :usernameOrEmail")
    Optional<PrepareUser> findByUsernameOrEmail(String usernameOrEmail);

    Optional<PrepareUser> findByUsername(String username);

    Optional<PrepareUser> findByEmail(String email);

    Optional<PrepareUser> findByConfirmToken(String confirmToken);
}
