package com.gravitlauncher.simplecabinet.web.dto.user;

import com.gravitlauncher.simplecabinet.web.model.user.UserReputationChange;

public class UserReputationChangeDto {
    private Long id;
    private Long userId;
    private Long targetId;
    private Long value;
    private UserReputationChange.ReputationChangeReason reason;

    public UserReputationChangeDto(Long id, Long userId, Long targetId, Long value, UserReputationChange.ReputationChangeReason reason) {
        this.id = id;
        this.userId = userId;
        this.targetId = targetId;
        this.value = value;
        this.reason = reason;
    }

    public UserReputationChangeDto(UserReputationChange entity) {
        this.id = entity.getId();
        this.userId = entity.getUser().getId();
        this.targetId = entity.getTarget().getId();
        this.value = entity.getValue();
        this.reason = entity.getReason();
    }
}
