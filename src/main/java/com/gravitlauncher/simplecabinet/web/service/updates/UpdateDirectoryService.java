package com.gravitlauncher.simplecabinet.web.service.updates;

import com.fasterxml.jackson.databind.JsonNode;
import com.gravitlauncher.simplecabinet.web.model.updates.UpdateDirectory;
import com.gravitlauncher.simplecabinet.web.repository.update.UpdateDirectoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateDirectoryService {
    @Autowired
    private UpdateDirectoryRepository repository;

    public UpdateDirectory create(JsonNode node) {
        UpdateDirectory directory = new UpdateDirectory();
        directory.setContent(node);
        return save(directory);
    }

    public UpdateDirectory getReferenceById(Long aLong) {
        return repository.getReferenceById(aLong);
    }

    public <S extends UpdateDirectory> S save(S entity) {
        return repository.save(entity);
    }

    public Optional<UpdateDirectory> findById(Long aLong) {
        return repository.findById(aLong);
    }
}
