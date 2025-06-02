package com.gravitlauncher.simplecabinet.web.model;

import com.gravitlauncher.simplecabinet.web.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity(name = "Audit")
@Table(name = "audit_log")
public class AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_log_generator")
    @SequenceGenerator(name = "audit_log_generator", sequenceName = "audit_log_seq", allocationSize = 1)
    private long id;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User target;
    @Setter
    private AuditType type;
    @Setter
    private LocalDateTime time;
    @Setter
    private String arg1;
    @Setter
    private String arg2;
    @Setter
    private String ip;

    public enum AuditType {
        UNKNOWN, CHANGE_PASSWORD, CHANGE_USERNAME, PASSWORD_RESET, CREATE_PRODUCT, DISABLE_2FA
    }

}
