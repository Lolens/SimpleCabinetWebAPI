package com.gravitlauncher.simplecabinet.web.service.user;

import com.gravitlauncher.simplecabinet.web.model.user.Group;
import com.gravitlauncher.simplecabinet.web.repository.user.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;


    public Group getReferenceById(String s) {
        return groupRepository.getReferenceById(s);
    }

    public Group create(String id, String displayName, String parentId) {
        var group = new Group();
        group.setId(id);
        group.setDisplayName(displayName);
        if (parentId != null) {
            group.setParent(groupRepository.getReferenceById(parentId));
        }
        return groupRepository.save(group);
    }

    public <S extends Group> S save(S entity) {
        return groupRepository.save(entity);
    }

    public Optional<Group> findById(String s) {
        return groupRepository.findById(s);
    }

    public Page<Group> findAll(Pageable pageable) {
        return groupRepository.findAll(pageable);
    }
}
