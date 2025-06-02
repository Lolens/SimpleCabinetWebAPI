package com.gravitlauncher.simplecabinet.web.service;

import com.gravitlauncher.simplecabinet.web.model.user.UserReputationChange;
import com.gravitlauncher.simplecabinet.web.repository.UserReputationChangeRepository;
import com.gravitlauncher.simplecabinet.web.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReputationService {
    @Autowired
    private UserReputationChangeRepository reputationChangeRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public boolean checkDuration(Long userId, Long targetId, Long delaySeconds) {
        LocalDateTime date = LocalDateTime.now().minusSeconds(delaySeconds);
        return reputationChangeRepository.findCountAfter(userId, targetId, date) == 0;
    }

    @Transactional
    public UserReputationChange change(Long userId, Long targetId, Long value, UserReputationChange.ReputationChangeReason reason) {
        UserReputationChange entity = new UserReputationChange();
        entity.setUser(userRepository.getReferenceById(userId));
        entity.setTarget(userRepository.getReferenceById(targetId));
        entity.setValue(value);
        entity.setReason(reason);
        entity.setDate(LocalDateTime.now());
        userRepository.changeReputation(userId, value);
        return reputationChangeRepository.save(entity);
    }
}
