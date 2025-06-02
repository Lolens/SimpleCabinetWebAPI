package com.gravitlauncher.simplecabinet.web;

import com.gravitlauncher.simplecabinet.web.model.user.UserGroup;
import com.gravitlauncher.simplecabinet.web.model.user.UserPermission;
import com.gravitlauncher.simplecabinet.web.service.user.UserDetailsService;
import com.gravitlauncher.simplecabinet.web.service.user.UserGroupService;
import com.gravitlauncher.simplecabinet.web.service.user.UserPermissionService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class PermissionTests {
    private static final String USERNAME = "PermTestUser";

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserService userService;

    @BeforeAll
    static void beforeAll(@Autowired UserService userService, @Autowired UserGroupService userGroupService, @Autowired UserPermissionService userPermissionService) {
        var user = userService.register(USERNAME, USERNAME + "@example.com", "1111");
        var group1 = new UserGroup();
        group1.setUser(user);
        group1.setGroupName("GR1");
        group1.setPriority(1);
        group1.setStartDate(LocalDateTime.now());
        userGroupService.save(group1);
        var group2 = new UserGroup();
        group2.setUser(user);
        group2.setGroupName("GR2");
        group2.setPriority(2);
        group2.setStartDate(LocalDateTime.now());
        userGroupService.save(group2);
        user.setGroups(List.of(group1, group2));
        {
            var perm = new UserPermission("GR1", "permtest.one", "one");
            userPermissionService.save(perm);
        }
        {
            var perm = new UserPermission("GR1", "permtest.two", "one");
            userPermissionService.save(perm);
        }
        {
            var perm = new UserPermission("GR2", "permtest.one", "two");
            userPermissionService.save(perm);
        }
        {
            var perm = new UserPermission("GR2", "permtest.none", "none");
            userPermissionService.save(perm);
        }
    }

    @Test
    @Transactional
    public void checkPermissionOrder() {
        var map = userDetailsService.collectUserPermissions(userService.getUserGroups(userService.findByUsername(USERNAME).orElseThrow()));
        Assertions.assertEquals(map.get("permtest.two"), "one");
        Assertions.assertEquals(map.get("permtest.one"), "two");
        Assertions.assertEquals(map.get("permtest.none"), "none");
    }
}
