package com.gravitlauncher.simplecabinet.web.dto.user;

import com.gravitlauncher.simplecabinet.web.model.user.UserSession;

import java.time.LocalDateTime;

public class UserSessionDto {
    public final long id;
    public final String client;
    public final LocalDateTime createdAt;
    public final boolean hardware;
    public final Long hardwareId;
    public final boolean active;

    public UserSessionDto(UserSession entity) {
        this(entity, false);
    }

    public UserSessionDto(UserSession entity, boolean showHardwareId) {
        this.id = entity.getId();
        this.client = entity.getClient();
        this.hardware = entity.getHardwareId() != null;
        if (showHardwareId) {
            this.hardwareId = entity.getHardwareId() != null ? entity.getHardwareId().getId() : 0;
        } else {
            this.hardwareId = null;
        }
        this.active = !entity.isDeleted();
        this.createdAt = entity.getCreatedAt();
    }
}
