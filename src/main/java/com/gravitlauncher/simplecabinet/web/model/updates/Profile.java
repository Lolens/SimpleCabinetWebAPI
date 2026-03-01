package com.gravitlauncher.simplecabinet.web.model.updates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "Profile")
@Table(name = "profiles")
public class Profile {
    @Getter
    @Setter
    @Id
    private UUID id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    @Column(name = "icon_id")
    private String iconId;
    @Getter
    @Setter
    @Column(name = "picture_id")
    private String pictureId;
    @Getter
    @Setter
    @Column(name = "large_picture_id")
    private String largePictureId;
    @Getter
    @Setter
    private boolean limited;
    @Getter
    @Setter
    private String tag;
    @Getter
    @Setter
    private boolean deleted;
}
