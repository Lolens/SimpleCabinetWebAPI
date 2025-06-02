package com.gravitlauncher.simplecabinet.web.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gravitlauncher.simplecabinet.web.model.user.Group;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserGroup;
import com.gravitlauncher.simplecabinet.web.repository.user.UserGroupRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserGroupService {
    @Autowired
    private UserGroupRepository repository;

    public UserGroup create(User user, Group group, LocalDateTime endDate) {
        var userGroup = new UserGroup();
        userGroup.setGroup(group);
        userGroup.setUser(user);
        userGroup.setStartDate(LocalDateTime.now());
        userGroup.setEndDate(endDate);
        return repository.save(userGroup);
    }

    public List<UserGroup> findByUser(User user) {
        return repository.findByUser(user, LocalDateTime.now());
    }

    public <S extends UserGroup> S save(S entity) {
        return repository.save(entity);
    }

    public Optional<UserGroup> findById(Long aLong) {
        return repository.findById(aLong);
    }

    public Optional<UserGroup> findByGroupAndUser(Group group, User user) {
        return repository.findByGroupAndUser(group, user, LocalDateTime.now());
    }

    public void delete(UserGroup userGroup) {
        repository.delete(userGroup);
    }
}
