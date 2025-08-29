package com.gravitlauncher.simplecabinet.web.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity(name = "UserReputationChange")
@Table(name = "user_rep_change")
public class UserReputationChange {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_rep_change_generator")
    @SequenceGenerator(name = "users_rep_change_generator", sequenceName = "users_rep_change_seq", allocationSize = 1)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "target_id")
    private User target;
    private Long value;
    private LocalDateTime date;
    private ReputationChangeReason reason;

    public enum ReputationChangeReason {
        NORMAL, SET
    }
}
