package com.gravitlauncher.simplecabinet.web.service;

import com.gravitlauncher.simplecabinet.web.configuration.properties.RegistrationConfig;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.model.user.PrepareUser;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.service.mail.MailService;
import com.gravitlauncher.simplecabinet.web.service.user.PasswordCheckService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import com.gravitlauncher.simplecabinet.web.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class RegisterService {
    private final Pattern USERNAME_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
    @Autowired
    private PrepareUserService prepareUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordCheckService passwordCheckService;
    @Autowired
    private MailService mailService;
    @Autowired
    private RegistrationConfig config;

    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Transactional
    public void check(String username, String email, String password) {
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new InvalidParametersException("Username contains forbidden characters", 33);
        }
        if (password.isEmpty()) {
            throw new InvalidParametersException("Empty password", 36);
        }
        if (password.length() < config.getMinPasswordLength() || password.length() > config.getMaxPasswordLength()) {
            throw new InvalidParametersException(String.format("Password length must be between %d and %d characters",
                    config.getMinPasswordLength(), config.getMaxPasswordLength()), 36);
        }
        if (prepareUserService.findByUsername(username).isPresent() || userService.findByUsername(username).isPresent()) {
            throw new InvalidParametersException("Username already registered", 34);
        }
        if (prepareUserService.findByEmail(email).isPresent() || userService.findByEmail(email).isPresent()) {
            throw new InvalidParametersException("Email already registered", 35);
        }
    }

    public RegisterResult register(String username, String email, String password) {
        long id;
        switch (config.getMode()) {
            case DEFAULT -> {
                var user = createUser(username, email, password);
                id = user.getId();
            }
            case ADMIN_ACCEPT -> {
                var user = createPrepareUser(username, email, password);
                id = user.getId();
            }
            case EMAIL_CONFIRM -> {
                var user = createPrepareUser(username, email, password);
                id = user.getId();
                mailService.sendTemplateEmail(user.getEmail(), "email-regconfirm.html", "%username%", URLEncoder.encode(username, StandardCharsets.UTF_8),
                        "%url%", String.format(config.getConfirmUrl(), user.getConfirmToken()));
            }
            default -> {
                throw new InvalidParametersException("Registration method not supported", 36);
            }
        }
        return new RegisterResult(config.getMode() == RegistrationConfig.RegistrationMode.DEFAULT,
                config.getMode() == RegistrationConfig.RegistrationMode.EMAIL_CONFIRM, id);
    }

    public Optional<User> confirm(String confirmToken) {
        var prepared = prepareUserService.findByConfirmToken(confirmToken);
        if (prepared.isEmpty()) {
            return Optional.empty();
        }
        var user = prepared.get();
        prepareUserService.delete(user);
        User user0 = new User();
        user0.setUsername(user.getUsername());
        user0.setUuid(UUID.randomUUID());
        user0.setEmail(user.getEmail());
        user0.setRawPassword(user.getPassword());
        user0.setRegistrationDate(LocalDateTime.now());
        user0.setGroups(new ArrayList<>());
        return Optional.of(userService.save(user0));
    }

    public User createUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        passwordCheckService.setPassword(user, password);
        user.setRegistrationDate(LocalDateTime.now());
        user.setGroups(new ArrayList<>());
        return userService.save(user);
    }

    public PrepareUser createPrepareUser(String username, String email, String password) {
        PrepareUser prepareUser = new PrepareUser();
        prepareUser.setUsername(username);
        prepareUser.setEmail(email);
        passwordCheckService.setPassword(prepareUser, password);
        prepareUser.setDate(LocalDateTime.now());
        prepareUser.setConfirmToken(SecurityUtils.generateRandomString(32));
        return prepareUserService.save(prepareUser);
    }

    public record RegisterResult(boolean prepared, boolean needConfirm, long id) {

    }
}
