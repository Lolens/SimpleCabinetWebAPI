package com.gravitlauncher.simplecabinet.web.controller;

import com.gravitlauncher.simplecabinet.web.dto.GroupDto;
import com.gravitlauncher.simplecabinet.web.dto.PageDto;
import com.gravitlauncher.simplecabinet.web.dto.user.UserPermissionDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.model.user.Group;
import com.gravitlauncher.simplecabinet.web.service.user.GroupService;
import com.gravitlauncher.simplecabinet.web.service.user.UserGroupService;
import com.gravitlauncher.simplecabinet.web.service.user.UserPermissionService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserPermissionService userPermissionService;

    @GetMapping("/id/{id}")
    public GroupDto findById(@PathVariable String id) {
        var optional = groupService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Group not found");
        }
        return optional.map(GroupDto::new).get();
    }

    @GetMapping("/id/{id}/permissions/page/{pageId}")
    public PageDto<UserPermissionDto> findPermissionsByGroupId(@PathVariable String id, @PathVariable int pageId) {
        var group = findGroupById(id);
        var list = userPermissionService.findByGroup(group, PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(UserPermissionDto::new));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/id/{id}/permissions/new")
    public UserPermissionDto createPermission(@PathVariable String id, @RequestBody CreatePermissionRequest request) {
        var group = findGroupById(id);
        var permission = userPermissionService.create(group, request.name(), request.value());
        return new UserPermissionDto(permission);
    }

    private Group findGroupById(@PathVariable String id) {
        Group group;
        if (id.equals("default")) {
            group = null;
        } else {
            var optional = groupService.findById(id);
            if (optional.isEmpty()) {
                throw new EntityNotFoundException("Group not found");
            }
            group = optional.get();
        }
        return group;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/permissions/id/{permissionId}")
    public void deletePermission(@PathVariable Long permissionId) {
        userPermissionService.delete(permissionId);
    }

    @GetMapping("/page/{pageId}")
    public PageDto<GroupDto> findAll(@PathVariable int pageId) {
        var list = groupService.findAll(PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(GroupDto::new));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/new")
    public GroupDto newGroup(@RequestBody CreateGroupRequest request) {
        var group = groupService.create(request.id(), request.displayName(), request.parentId());
        return new GroupDto(group);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/id/{id}/assign")
    public void assign(@PathVariable String id, @RequestBody AssignRequest request) {
        var group = groupService.findById(id);
        if (group.isEmpty()) {
            throw new EntityNotFoundException("Group not found");
        }
        var user = userService.findById(request.userId());
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        userGroupService.create(user.get(), group.get(), request.endDate());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/id/{id}/unassign")
    public void unassign(@PathVariable String id, @RequestBody UnassignRequest request) {
        var group = groupService.findById(id);
        if (group.isEmpty()) {
            throw new EntityNotFoundException("Group not found");
        }
        var user = userService.findById(request.userId());
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        var userGroup = userGroupService.findByGroupAndUser(group.get(), user.get());
        if (userGroup.isEmpty()) {
            throw new InvalidParametersException("UserGroup not found", 40);
        }
    }

    public record CreateGroupRequest(String id, String displayName, String parentId) {

    }

    public record AssignRequest(Long userId, LocalDateTime endDate) {

    }

    public record UnassignRequest(Long userId) {

    }

    public record CreatePermissionRequest(String name, String value) {

    }
}
