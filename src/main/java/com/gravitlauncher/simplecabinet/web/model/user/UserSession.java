package com.gravitlauncher.simplecabinet.web.model.user;

import io.hypersistence.utils.hibernate.type.basic.Inet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity(name = "UserSession")
@Table(name = "sessions")
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sessions_generator")
    @SequenceGenerator(name = "sessions_generator", sequenceName = "sessions_seq", allocationSize = 1)
    private long id;
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Setter
    @Column(name = "refresh_token")
    private String refreshToken;
    @Setter
    @Column(columnDefinition = "inet")
    private Inet ip;
    @Setter
    private String client;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hwid_id")
    private HardwareId hardwareId;
    @Setter
    @Column(name = "server_id")
    private String serverId;
    @Setter
    private boolean deleted;
    @Setter
    private LocalDateTime createdAt;

}
