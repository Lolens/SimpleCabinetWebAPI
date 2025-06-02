package com.gravitlauncher.simplecabinet.web.service;

import com.gravitlauncher.simplecabinet.web.model.Server;
import com.gravitlauncher.simplecabinet.web.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ServerService {
    @Autowired
    private ServerRepository serverRepository;

    public Server create(String name, String displayName) {
        Server server = new Server();
        server.setName(name);
        server.setDisplayName(displayName);
        return serverRepository.save(server);
    }

    public Optional<Server> findByName(String name) {
        return serverRepository.findByName(name);
    }

    public <S extends Server> S save(S entity) {
        return serverRepository.save(entity);
    }

    public Optional<Server> findById(Long aLong) {
        return serverRepository.findById(aLong);
    }

    public void ping(String name, int online, int maxOnline, int tps, List<String> users) {
        serverRepository.ping(name, online, maxOnline, tps, users, LocalDateTime.now());
    }

    public void ping(long id, int online, int maxOnline, int tps, List<String> users) {
        serverRepository.ping(id, online, maxOnline, tps, users, LocalDateTime.now());
    }

    public Page<Server> findAll(Pageable pageable) {
        return serverRepository.findAll(pageable);
    }
}
