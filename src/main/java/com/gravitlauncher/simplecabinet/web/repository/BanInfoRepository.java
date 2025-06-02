package com.gravitlauncher.simplecabinet.web.repository;

import com.gravitlauncher.simplecabinet.web.model.BanInfoEntity;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BanInfoRepository extends JpaRepository<BanInfoEntity, Long> {
    Page<BanInfoEntity> findAllByShadow(boolean shadow, Pageable pageable);

    @Query("select m from BanInfo m where m.target = ?1 and ( m.endAt is null or m.endAt > ?2 )")
    Optional<BanInfoEntity> findBanByUser(User target, LocalDateTime now);
}
