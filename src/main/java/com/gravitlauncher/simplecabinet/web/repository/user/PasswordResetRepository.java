package com.gravitlauncher.simplecabinet.web.repository.user;

import com.gravitlauncher.simplecabinet.web.model.user.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
}
