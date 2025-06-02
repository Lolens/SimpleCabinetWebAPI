package com.gravitlauncher.simplecabinet.web.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.gravitlauncher.simplecabinet.web.model.user.Group;
import com.gravitlauncher.simplecabinet.web.model.user.UserPermission;
import com.gravitlauncher.simplecabinet.web.repository.user.UserPermissionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserPermissionService {
    @Autowired
    private UserPermissionRepository repository;

    public UserPermission create(Group group, String name, String value) {
        var permission = new UserPermission();
        permission.setGroup(group);
        permission.setName(name);
        permission.setValue(value);
        return repository.save(permission);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<UserPermission> findByGroup(Group group) {
        return repository.findByGroup(group);
    }

    public Page<UserPermission> findByGroup(Group group, Pageable pageable) {
        return repository.findByGroup(group, pageable);
    }

    public List<UserPermission> findByUser(Long userId) {
        return repository.findByUser(userId, LocalDateTime.now());
    }

    public <S extends UserPermission> S save(S entity) {
        return repository.save(entity);
    }

    public Optional<UserPermission> findById(Long aLong) {
        return repository.findById(aLong);
    }
}
