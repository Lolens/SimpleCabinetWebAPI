package com.gravitlauncher.simplecabinet.web.service;

import com.gravitlauncher.simplecabinet.web.model.user.PrepareUser;
import com.gravitlauncher.simplecabinet.web.repository.user.PrepareUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PrepareUserService {
    @Autowired
    private PrepareUserRepository prepareUserRepository;

    public Optional<PrepareUser> findByUsernameOrEmail(String usernameOrEmail) {
        return prepareUserRepository.findByUsernameOrEmail(usernameOrEmail);
    }

    public Optional<PrepareUser> findByConfirmToken(String confirmToken) {
        return prepareUserRepository.findByConfirmToken(confirmToken);
    }

    public <S extends PrepareUser> S save(S entity) {
        return prepareUserRepository.save(entity);
    }

    public Optional<PrepareUser> findById(Long aLong) {
        return prepareUserRepository.findById(aLong);
    }

    public void deleteById(Long aLong) {
        prepareUserRepository.deleteById(aLong);
    }

    public void delete(PrepareUser entity) {
        prepareUserRepository.delete(entity);
    }

    public Optional<PrepareUser> findByUsername(String username) {
        return prepareUserRepository.findByUsername(username);
    }

    public Optional<PrepareUser> findByEmail(String email) {
        return prepareUserRepository.findByEmail(email);
    }
}
