package com.gravitlauncher.simplecabinet.web.dto;

import com.gravitlauncher.simplecabinet.web.model.Server;

import java.time.LocalDateTime;
import java.util.List;

public class ServerDto {
    public final long id;
    public final String name;
    public final String displayName;
    public final int maxOnline;
    public final int online;
    public final int tps;
    public final List<String> users;
    public final LocalDateTime updateDate;

    public ServerDto(Server server) {
        this.id = server.getId();
        this.name = server.getName();
        this.displayName = server.getDisplayName();
        this.maxOnline = server.getMaxOnline();
        this.online = server.getOnline();
        this.tps = server.getTps();
        this.users = server.getUsers();
        this.updateDate = server.getUpdateDate();
    }
}
