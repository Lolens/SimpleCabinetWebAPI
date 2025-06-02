package com.gravitlauncher.simplecabinet.web.controller.admin;

import com.gravitlauncher.simplecabinet.web.configuration.jwt.JwtProvider;
import com.gravitlauncher.simplecabinet.web.dto.user.UserDto;
import com.gravitlauncher.simplecabinet.web.dto.user.UserSessionDto;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.user.SessionService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/server")
public class ServerCheckController {
    @Autowired
    public UserService userService;
    @Autowired
    public SessionService sessionService;
    @Autowired
    public DtoService dtoService;
    @Autowired
    public JwtProvider jwtProvider;

    @PostMapping("/joinserver")
    public JoinServerResponse joinServer(@RequestBody JoinServerRequest request) {
        var sessionOptional = sessionService.findById(Long.valueOf(request.sessionId));
        if (sessionOptional.isEmpty()) {
            throw new InvalidParametersException("Session not found", 5);
        }
        var session = sessionOptional.get();
        session.setServerId(request.serverID);
        sessionService.save(session);
        return new JoinServerResponse(true);
    }

    @PostMapping("/publicjoinserver")
    public JoinServerResponse publicJoinServer(@RequestBody JoinServerPublicRequest request) {
        var details = jwtProvider.getDetailsFromToken(request.accessToken);
        if (!details.getUsername().equals(request.username)) {
            return new JoinServerResponse(false);
        }
        var sessionOptional = sessionService.findById(details.getSessionId());
        if (sessionOptional.isEmpty()) {
            throw new InvalidParametersException("Session not found", 5);
        }
        var session = sessionOptional.get();
        session.setServerId(request.serverID);
        sessionService.save(session);
        return new JoinServerResponse(true);
    }

    @PostMapping("/checkserver")
    @Transactional
    public UserDto checkServer(@RequestBody CheckServerRequest request) {
        var userOptional = userService.findByUsername(request.username);
        if (userOptional.isEmpty()) {
            throw new InvalidParametersException("User not found", 1);
        }
        var sessionOptional = sessionService.findByUserAndServerId(userOptional.get(), request.serverID);
        if (sessionOptional.isEmpty()) {
            throw new InvalidParametersException("Session not found", 5);
        }
        return dtoService.toPublicUserDto(userOptional.get());
    }

    @PostMapping("/extendedcheckserver")
    @Transactional
    public ExtendedCheckServerResponse extendedCheckServer(@RequestBody CheckServerRequest request) {
        var userOptional = userService.findByUsername(request.username);
        if (userOptional.isEmpty()) {
            throw new InvalidParametersException("User not found", 1);
        }
        var sessionOptional = sessionService.findByUserAndServerId(userOptional.get(), request.serverID);
        if (sessionOptional.isEmpty()) {
            throw new InvalidParametersException("Session not found", 5);
        }
        return new ExtendedCheckServerResponse(dtoService.toPublicUserDto(userOptional.get()), new UserSessionDto(sessionOptional.get(), true));
    }

    public record JoinServerRequest(String sessionId, String serverID) {
    }

    public record JoinServerPublicRequest(String username, String accessToken, String serverID) {
    }

    public record JoinServerResponse(boolean success) {
    }

    public record CheckServerRequest(String username, String serverID) {
    }

    public record ExtendedCheckServerResponse(UserDto user, UserSessionDto session) {
    }
}
