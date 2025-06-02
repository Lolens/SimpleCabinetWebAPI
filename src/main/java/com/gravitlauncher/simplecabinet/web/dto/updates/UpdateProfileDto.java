package com.gravitlauncher.simplecabinet.web.dto.updates;

import com.fasterxml.jackson.databind.JsonNode;
import com.gravitlauncher.simplecabinet.web.model.updates.UpdateProfile;

import java.time.LocalDateTime;

public class UpdateProfileDto {
    private final Long id;
    private final Long assetsDirId;
    private final Long clientDirId;
    private final String tag;
    private final JsonNode content;
    private final LocalDateTime createdAt;

    public UpdateProfileDto(Long id, Long assetsDirId, Long clientDirId, String tag, JsonNode content, LocalDateTime createdAt) {
        this.id = id;
        this.assetsDirId = assetsDirId;
        this.clientDirId = clientDirId;
        this.tag = tag;
        this.content = content;
        this.createdAt = createdAt;
    }


    public UpdateProfileDto(UpdateProfile profile) {
        this.id = profile.getId();
        this.assetsDirId = profile.getAssets().getId();
        this.clientDirId = profile.getClient().getId();
        this.tag = profile.getTag();
        this.content = profile.getContent();
        this.createdAt = profile.getCreatedAt();
    }
}
