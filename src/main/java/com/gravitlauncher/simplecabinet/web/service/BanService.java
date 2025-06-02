package com.gravitlauncher.simplecabinet.web.service;

import com.gravitlauncher.simplecabinet.web.model.BanInfoEntity;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.repository.BanInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BanService {
    private final Logger logger = LoggerFactory.getLogger(BanService.class);
    @Autowired
    private BanInfoRepository repository;

    public Page<BanInfoEntity> findAll(Pageable pageable) {
        return repository.findAllByShadow(false, pageable);
    }

    public <S extends BanInfoEntity> S save(S entity) {
        return repository.save(entity);
    }

    @Transactional
    public BanInfoEntity ban(User target, User moderator, String reason, LocalDateTime endDate) {
        BanInfoEntity entity = new BanInfoEntity();
        entity.setTarget(target);
        entity.setModerator(moderator);
        entity.setReason(reason);
        var now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setEndAt(endDate);
        repository.save(entity);
        return entity;
    }

    @Transactional
    public Optional<BanInfoEntity> findBanByUser(User user) {
        return repository.findBanByUser(user, LocalDateTime.now());
    }

    @Transactional
    public void unban(BanInfoEntity entity) {
        entity.setEndAt(LocalDateTime.now().minus(Duration.ofSeconds(1)));
        repository.save(entity);
    }

    public Optional<BanInfoEntity> findById(Long aLong) {
        return repository.findById(aLong);
    }

    public void delete(BanInfoEntity entity) {
        repository.delete(entity);
    }
}
