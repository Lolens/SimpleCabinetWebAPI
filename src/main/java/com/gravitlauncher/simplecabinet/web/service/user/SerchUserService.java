package com.gravitlauncher.simplecabinet.web.service.user;

import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.repository.user.UserSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SerchUserService {
    @Autowired
    private UserSearchRepository repository;

    public Page<User> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<User> findByUsernameFetchAssets(String username,Pageable pageable) {
        return repository.findByUsernameFetchAssets(username, pageable);
    }

    public Page<User> findByEmail(String email,Pageable pageable) {
        return repository.findByEmail(email,pageable);
    }

}


