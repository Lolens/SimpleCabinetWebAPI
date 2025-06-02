package com.gravitlauncher.simplecabinet.web.controller;

import com.gravitlauncher.simplecabinet.web.dto.PageDto;
import com.gravitlauncher.simplecabinet.web.dto.ServerDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.model.Server;
import com.gravitlauncher.simplecabinet.web.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/servers")
public class ServersController {
    @Autowired
    private ServerService service;

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ServerDto create(@RequestBody CreateServerRequest request) {
        Server newServer = new Server();
        newServer.setName(request.name);
        newServer.setDisplayName(request.displayName);
        service.save(newServer);
        return new ServerDto(newServer);

    }
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ServerDto update(@RequestBody UpdateServerRequest request) {
        Optional<Server> existingServerOptional = service.findByName(request.name());
        Server serverUpdate = existingServerOptional.get();
        serverUpdate.setName(request.name);
        serverUpdate.setDisplayName(request.displayName);
        service.save(serverUpdate);
        return new ServerDto(serverUpdate);
    }

    @GetMapping("/id/{serverId}")
    public ServerDto getById(@PathVariable long serverId) {
        var optional = service.findById(serverId);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Server not found");
        }
        return new ServerDto(optional.get());
    }

    @PostMapping("/id/{serverId}/ping")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void pingById(@PathVariable long serverId, @RequestBody PingRequest request) {
        service.ping(serverId, request.online(), request.maxOnline(), request.tps(), request.users());
    }

    @GetMapping("/name/{name}")
    public ServerDto getByName(@PathVariable String name) {
        var optional = service.findByName(name);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Server not found");
        }
        return new ServerDto(optional.get());
    }

    @PostMapping("/name/{name}/ping")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void pingByName(@PathVariable String name, @RequestBody PingRequest request) {
        service.ping(name, request.online(), request.maxOnline(), request.tps(), request.users());
    }

    @GetMapping("/page/{pageId}")
    public PageDto<ServerDto> getPage(@PathVariable int pageId) {
        var list = service.findAll(PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(ServerDto::new));
    }

    public record PingRequest(int online, int maxOnline, int tps, List<String> users) {

    }

    public record CreateServerRequest(String name, String displayName) {

    }
    public record UpdateServerRequest(String name, String displayName) {

    }
}
