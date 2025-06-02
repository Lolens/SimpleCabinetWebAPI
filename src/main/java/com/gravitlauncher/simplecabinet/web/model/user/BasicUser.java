package com.gravitlauncher.simplecabinet.web.model.user;

import java.util.UUID;

public interface BasicUser {
    long getId();

    String getUsername();

    UUID getUuid();
}
