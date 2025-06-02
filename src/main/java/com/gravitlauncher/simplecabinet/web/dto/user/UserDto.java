package com.gravitlauncher.simplecabinet.web.dto.user;

import com.gravitlauncher.simplecabinet.web.model.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserDto {
    public final long id;
    public final String username;
    public final UUID uuid;
    public final User.Gender gender;
    public final Long reputation;
    public final String status;
    public final LocalDateTime registrationDate;
    public final List<UserGroupDto> groups;
    public final Map<String, UserTexture> assets;
    public final Map<String, String> permissions;

    public UserDto(long id, String username, UUID uuid, User.Gender gender, Long reputation, String status, LocalDateTime registrationDate, List<UserGroupDto> groups, Map<String, UserTexture> assets, Map<String, String> permissions) {
        this.id = id;
        this.username = username;
        this.uuid = uuid;
        this.gender = gender;
        this.reputation = reputation;
        this.status = status;
        this.registrationDate = registrationDate;
        this.groups = groups;
        this.assets = assets;
        this.permissions = permissions;
    }

    public static class UserTexture {
        public final String url;
        public final String digest;
        public final Map<String, String> metadata;

        public UserTexture(String url, String digest, Map<String, String> metadata) {
            this.url = url;
            this.digest = digest;
            this.metadata = metadata;
        }
    }
}
