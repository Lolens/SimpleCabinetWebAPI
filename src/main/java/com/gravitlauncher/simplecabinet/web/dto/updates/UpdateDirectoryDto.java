package com.gravitlauncher.simplecabinet.web.dto.updates;

import com.gravitlauncher.simplecabinet.web.model.updates.UpdateDirectory;

public class UpdateDirectoryDto {
    public final Long id;

    public UpdateDirectoryDto(Long id) {
        this.id = id;
    }

    public UpdateDirectoryDto(UpdateDirectory updateDirectory) {
        this.id = updateDirectory.getId();
    }
}
