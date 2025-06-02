package com.gravitlauncher.simplecabinet.web.repository;

import com.gravitlauncher.simplecabinet.web.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ServerRepository extends JpaRepository<Server, Long> {
    Optional<Server> findByName(String name);

    @Modifying
    @Query("update Server s set s.online = ?2, s.maxOnline = ?3, s.tps = ?4, s.users = ?5, s.updateDate = ?6 where s.name = ?1")
    @Transactional
    void ping(String name, int online, int maxOnline, int tps, List<String> users, LocalDateTime updateDate);

    @Modifying
    @Query("update Server s set s.online = ?2, s.maxOnline = ?3, s.tps = ?4, s.users = ?5, s.updateDate = ?6 where s.id = ?1")
    void ping(long id, int online, int maxOnline, int tps, List<String> users, LocalDateTime updateDate);
}
