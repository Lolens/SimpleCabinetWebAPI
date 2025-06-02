package com.gravitlauncher.simplecabinet.web.controller;

import com.gravitlauncher.simplecabinet.web.configuration.jwt.JwtProvider;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserGroup;
import com.gravitlauncher.simplecabinet.web.service.RegisterService;
import com.gravitlauncher.simplecabinet.web.service.user.*;
import com.gravitlauncher.simplecabinet.web.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
public class SetupController {
    @Autowired
    public UserService userService;
    @Autowired
    public GroupService groupService;
    @Autowired
    public UserGroupService userGroupService;
    @Autowired
    public SessionService sessionService;
    @Autowired
    public JwtProvider jwtProvider;
    @Autowired
    public RegisterService registerService;
    @Autowired
    private PasswordCheckService passwordCheckService;

    @GetMapping("/setup")
    public SetupResponse setup() {
        if (userService.findById(1L).isPresent()) {
            throw new InvalidParametersException("Setup is completed", 19);
        }
        var adminGroup = groupService.create("ADMIN", "Administrator", null);
        var password = SecurityUtils.generateRandomString(32);
        User user = registerService.createUser("admin", "admin@example.com", password);
        UserGroup admin = new UserGroup();
        admin.setUser(user);
        admin.setGroup(adminGroup);
        admin.setStartDate(LocalDateTime.now());
        userGroupService.save(admin);
        user.setGroups(new ArrayList<>());
        user.getGroups().add(admin);
        userService.save(user);
        var session = sessionService.create(user, "Setup Session", "127.0.0.1");
        var token = jwtProvider.generateNoExpiredJWTToken(session);
        return new SetupResponse(user.getUsername(), password, token.token());
    }

    @GetMapping("/myip")
    public String myIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    public record SetupResponse(String username, String password, String accessToken) {

    }
}
