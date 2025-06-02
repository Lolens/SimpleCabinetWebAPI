package com.gravitlauncher.simplecabinet.web.service.user;

import com.gravitlauncher.simplecabinet.web.model.user.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gravitlauncher.simplecabinet.web.model.user.*;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserDetailsService {
    @Autowired
    private UserService service;
    @Autowired
    private UserPermissionService permissionService;
    @Autowired
    private UserGroupService userGroupService;

    public CabinetUserDetails create(long userId, String username, List<String> roles, String client, long sessionId) {
        return new CabinetUserDetails(userId, null, username, roles, client, sessionId);
    }

    public CabinetUserDetails create(UserSession session) {
        var groups = userGroupService.findByUser(session.getUser());
        return new CabinetUserDetails(session.getUser().getId(), null,
                session.getUser().getUsername(),
                groups.stream().map(UserGroup::getGroup).map(Group::getId).toList(),
                session.getClient(), session.getId());
    }

    @Transactional
    public CabinetUserDetails makeDetails(User entity) {
        var groups = userGroupService.findByUser(entity);
        return new UserDetailsService.CabinetUserDetails(
                entity.getId(),
                entity.getPassword(),
                entity.getUsername(),
                groups.stream().map(UserGroup::getGroup).map(Group::getId).toList(),
                "UNKNOWN",
                0
        );
    }

    public Map<String, String> getUserPermissions(User user) {
        return permissionService.findByUser(user.getId()).stream().collect(Collectors.toMap(UserPermission::getName, UserPermission::getValue));
    }

    public class CabinetUserDetails implements UserDetails {
        @Getter
        private final long userId;
        private final String password;
        private final String username;
        private final List<GrantedAuthority> authorities;
        @Getter
        private final String client;
        @Getter
        private final long sessionId;
        private Map<String, UserPermission> permissions;

        public CabinetUserDetails(long userId, String password, String username, List<String> roles, String client, long sessionId) {
            this.userId = userId;
            this.password = password;
            this.username = username;
            this.authorities = roles.stream().map(e -> new SimpleGrantedAuthority("ROLE_".concat(e.toUpperCase(Locale.ROOT)))).collect(Collectors.toList());
            this.client = client;
            this.sessionId = sessionId;
        }

        public Map<String, UserPermission> getPermissions() { // Optimize this
            if (permissions == null) {
                var user = service.getReference(userId);
                permissions = permissionService.findByUser(userId).stream().collect(Collectors.toMap(UserPermission::getName, e -> e));
            }
            return permissions;
        }

        public String getPermission(String name) {
            return getPermission(name, null);
        }

        public String getPermission(String name, String defaultValue) {
            var permissions = getPermissions();
            var permission = permissions.get(name);
            if (permission == null) {
                return defaultValue;
            }
            return permission.getValue();
        }

        public boolean checkAuthority(String name) {
            for (var e : authorities) {
                if (e.getAuthority().equals(name)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
