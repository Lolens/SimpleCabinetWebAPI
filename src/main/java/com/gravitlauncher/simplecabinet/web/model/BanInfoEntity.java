package com.gravitlauncher.simplecabinet.web.model;

import com.gravitlauncher.simplecabinet.web.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity(name = "BanInfo")
@Table(name = "baninfo")
public class BanInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baninfo_generator")
    @SequenceGenerator(name = "baninfo_generator", sequenceName = "baninfo_seq", allocationSize = 1)
    private long id;
    @Setter
    @ManyToOne
    @JoinColumn(name = "target_id")
    private User target;
    @Setter
    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private User moderator;
    @Setter
    private String reason;
    @Setter
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Setter
    @Column(name = "end_at")
    private LocalDateTime endAt;
    @Setter
    private boolean shadow;

}
