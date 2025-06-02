package com.gravitlauncher.simplecabinet.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gravitlauncher.simplecabinet.web.model.user.UserReputationChange;

import java.time.LocalDateTime;

public interface UserReputationChangeRepository extends JpaRepository<UserReputationChange, Long> {
    @Query("select count(rep.id) from UserReputationChange rep where rep.user.id = :userId and rep.target.id = :targetId and rep.date > :date")
    int findCountAfter(Long userId, Long targetId, LocalDateTime date);
}
