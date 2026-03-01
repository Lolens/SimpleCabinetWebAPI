package com.gravitlauncher.simplecabinet.web.model.updates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "launcher_artifacts")
public class LauncherArtifact {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "launcher_artifacts_generator")
    @SequenceGenerator(name = "launcher_artifacts_generator", sequenceName = "launcher_artifacts_seq", allocationSize = 1)
    private long id;
    @Getter
    @Setter
    @Column(name = "artifact_id")
    private String artifactId;
    @Getter
    @Setter
    @Column(name = "artifact_type")
    private String artifactType;
    @Getter
    @Setter
    @Column(name = "upload_at")
    private LocalDateTime uploadAt;
    @Getter
    @Setter
    @Column(name = "public_key")
    private byte[] publicKey;
    @Getter
    @Setter
    @Column(name = "deprecated")
    private boolean deprecated;
}
