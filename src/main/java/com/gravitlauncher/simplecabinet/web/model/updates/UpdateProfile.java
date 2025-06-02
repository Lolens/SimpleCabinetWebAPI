package com.gravitlauncher.simplecabinet.web.model.updates;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity(name = "UpdateProfile")
@Table(name = "update_profiles")
public class UpdateProfile {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "update_profiles_generator")
    @SequenceGenerator(name = "update_profiles_generator", sequenceName = "update_profiles_seq", allocationSize = 1)
    private long id;
    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_update_id")
    private UpdateDirectory client;
    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_update_id")
    private UpdateDirectory assets;
    @Getter
    @Setter
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode content;
    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_id")
    private UpdateProfile previous;
    @Getter
    @Setter
    private String tag;
    @Getter
    @Setter
    private LocalDateTime createdAt;
}
