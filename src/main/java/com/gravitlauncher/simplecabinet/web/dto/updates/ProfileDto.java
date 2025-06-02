package com.gravitlauncher.simplecabinet.web.dto.updates;

import java.util.UUID;

public class ProfileDto {
    public final UUID id;
    public final String name;
    public final String description;
    public final String iconId;
    public final String pictureId;
    public final String largePictureId;
    public final boolean limited;
    public final String tag;

    public ProfileDto(UUID id, String name, String description, String iconId, String pictureId, String largePictureId, boolean limited, String tag) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconId = iconId;
        this.pictureId = pictureId;
        this.largePictureId = largePictureId;
        this.limited = limited;
        this.tag = tag;
    }
}
