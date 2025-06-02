package com.gravitlauncher.simplecabinet.web.service.user;

import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserSession;
import com.gravitlauncher.simplecabinet.web.repository.user.UserSessionRepository;
import com.gravitlauncher.simplecabinet.web.utils.SecurityUtils;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SessionService {
    @Autowired
    private UserSessionRepository repository;
    @PersistenceContext
    private EntityManager entityManager;

    public UserSession create(User user, String client, String ip) {
        UserSession session = new UserSession();
        session.setUser(user);
        session.setClient(client);
        session.setRefreshToken(SecurityUtils.generateRandomString(32));
        session.setCreatedAt(LocalDateTime.now());
        session.setIp(new Inet(ip));
        repository.save(session);
        return session;
    }

    public <S extends UserSession> S save(S entity) {
        return repository.save(entity);
    }

    @Transactional
    public Optional<UserSession> updateRefreshToken(String refreshToken) {
        String newToken = SecurityUtils.generateRandomString(32);
        int count = repository.refreshSession(newToken, refreshToken);
        if (count > 0) {
            var result = repository.findByRefreshToken(newToken);
            result.ifPresent((x) -> x.setRefreshToken(newToken)); // Cache (?)
            return result;
        }
        return Optional.empty();
    }

    public Optional<UserSession> findByRefreshToken(String refreshToken) {
        return repository.findByRefreshToken(refreshToken);
    }

    public Optional<UserSession> findByUserAndServerId(User user, String serverId) {
        return repository.findByUserAndServerId(user, serverId);
    }

    public Optional<UserSession> findByServerId(String serverId) {
        return repository.findByServerId(serverId);
    }

    public Page<UserSession> findByUser(User user, Pageable pageable) {
        return repository.findByUser(user, pageable);
    }

    public Page<UserSession> findByUserPublic(User user, Pageable pageable) {
        return repository.findByUserAndDeleted(user, false, pageable);
    }

    @Transactional
    public void deactivateAllByUser(User user) {
        var query = entityManager.createQuery("update UserSession s set s.deleted = true where s.user = :user");
        query.setParameter("user", user);
        query.executeUpdate();
    }

    @Transactional
    public void deactivateAllByUserWithExclude(User user, Long excludeSessionId) {
        var query = entityManager.createQuery("update UserSession s set s.deleted = true where s.user = :user and s.id != :excludeSessionId");
        query.setParameter("user", user);
        query.setParameter("excludeSessionId", excludeSessionId);
        query.executeUpdate();
    }

    public Optional<UserSession> findById(Long aLong) {
        return repository.findById(aLong);
    }

    public void delete(UserSession entity) {
        repository.delete(entity);
    }

    @Transactional
    public boolean deactivateById(long id) {
        return repository.deactivateById(id) > 0;
    }
}
