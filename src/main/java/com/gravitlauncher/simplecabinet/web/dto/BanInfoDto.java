package com.gravitlauncher.simplecabinet.web.dto;

import com.gravitlauncher.simplecabinet.web.model.BanInfoEntity;

import java.time.LocalDateTime;

public class BanInfoDto {
    public final long id;
    public final long targetId;
    public final long moderatorId;
    public final String targetUsername;
    public final String moderatorUsername;
    public final String reason;
    public final LocalDateTime createdAt;
    public final LocalDateTime endAt;

    public BanInfoDto(BanInfoEntity entity) {
        this.id = entity.getId();
        this.targetId = entity.getTarget().getId();
        this.targetUsername = entity.getTarget().getUsername();
        this.moderatorId = entity.getModerator().getId();
        this.moderatorUsername = entity.getModerator().getUsername();
        this.reason = entity.getReason();
        this.createdAt = entity.getCreatedAt();
        this.endAt = entity.getEndAt();
    }
}
