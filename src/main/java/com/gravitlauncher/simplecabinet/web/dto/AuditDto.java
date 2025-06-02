package com.gravitlauncher.simplecabinet.web.dto;

import com.gravitlauncher.simplecabinet.web.model.AuditEntity;

import java.time.LocalDateTime;

public class AuditDto {
    public final long id;
    public final long userId;
    public final long targetUserId;
    public final AuditEntity.AuditType type;
    public final LocalDateTime time;
    public final String arg1;
    public final String arg2;
    public final String ip;

    public AuditDto(AuditEntity entity) {
        this.id = entity.getId();
        this.userId = entity.getUser().getId();
        this.targetUserId = entity.getTarget().getId();
        this.type = entity.getType();
        this.time = entity.getTime();
        this.arg1 = entity.getArg1();
        this.arg2 = entity.getArg2();
        this.ip = entity.getIp();
    }
}
