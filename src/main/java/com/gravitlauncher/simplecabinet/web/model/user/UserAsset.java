package com.gravitlauncher.simplecabinet.web.model.user;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Getter
@Entity(name = "UserAsset")
@Table(name = "user_assets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "type"})  // У пользователя может быть только одна текстура каждого типа
})
public class UserAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_assets_generator")
    @SequenceGenerator(name = "user_assets_generator", sequenceName = "user_assets_seq", allocationSize = 1)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AssetType type;

    @Setter
    @Column(name = "digest", nullable = false, length = 64)
    private String digest; // SHA256 хэш файла

    @Setter
    @Column(name = "file_name", nullable = false)
    private String fileName; // digest + ".png"

    @Setter
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Setter
    @Column(name = "width")
    private Integer width;

    @Setter
    @Column(name = "height")
    private Integer height;

    @Setter
    @Type(JsonType.class)
    @Column(name = "metadata", columnDefinition = "JSON")
    private JsonNode metadata;

    @Setter
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.fileName == null && this.digest != null) {
            this.fileName = this.digest + ".png";
        }
        if (this.uploadedAt == null) {
            this.uploadedAt = LocalDateTime.now();
        }
    }

    public String getStoragePath() {
        return this.type.getFolder() + "/" + this.fileName;
    }

    public String getUrl() {
        return this.type.getUrlPath() + this.fileName;
    }

    public enum AssetType {
        SKIN("skins"),
        CAPE("capes"),
        AVATAR("avatars");

        private final String folder;

        AssetType(String folder) {
            this.folder = folder;
        }

        public String getFolder() {
            return folder;
        }

        public String getUrlPath() {
            return "/assets/" + folder + "/";
        }

        public static AssetType fromString(String name) {
            try {
                return AssetType.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}