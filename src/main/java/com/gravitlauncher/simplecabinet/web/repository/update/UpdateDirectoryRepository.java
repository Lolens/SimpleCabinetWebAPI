package com.gravitlauncher.simplecabinet.web.repository.update;

import com.gravitlauncher.simplecabinet.web.model.updates.UpdateDirectory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpdateDirectoryRepository extends JpaRepository<UpdateDirectory, Long> {
}
