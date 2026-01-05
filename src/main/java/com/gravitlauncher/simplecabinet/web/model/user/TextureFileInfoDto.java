package com.gravitlauncher.simplecabinet.web.model.user;

import java.util.List;

public record TextureFileInfoDto(
        String digest,
        int totalRecords,
        int uniqueUsers,
        List<UserUsage> usageByUser
) {
    public record UserUsage(long userId, String username, List<String> types) {}
}