package com.gravitlauncher.simplecabinet.web.controller.launcher;

import com.gravitlauncher.simplecabinet.web.configuration.jwt.JwtProvider;
import com.gravitlauncher.simplecabinet.web.controller.admin.ServerCheckController;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.user.SessionService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/launcher/auth")
public class LauncherCheckServerController {
    @Autowired
    public UserService userService;
    @Autowired
    public SessionService sessionService;
    @Autowired
    public JwtProvider jwtProvider;
    @Autowired
    private DtoService dtoService;

    @PostMapping("/joinserver/username")
    public ServerCheckController.JoinServerResponse joinServerByUsername(@RequestBody JoinServerPublicRequest request) {
        var details = jwtProvider.getDetailsFromToken(request.accessToken);
        if (!details.getUsername().equals(request.username)) {
            return new ServerCheckController.JoinServerResponse(false);
        }
        var sessionOptional = sessionService.findById(details.getSessionId());
        if (sessionOptional.isEmpty()) {
            throw new InvalidParametersException("Session not found", 5);
        }
        var session = sessionOptional.get();
        session.setServerId(request.serverID);
        sessionService.save(session);
        return new ServerCheckController.JoinServerResponse(true);
    }

    @PostMapping("/joinserver/uuid")
    public ServerCheckController.JoinServerResponse joinServerByUuid(@RequestBody JoinServerPublicUuidRequest request) {
        var details = jwtProvider.getDetailsFromToken(request.accessToken);
        var sessionOptional = sessionService.findById(details.getSessionId());
        if (sessionOptional.isEmpty()) {
            throw new InvalidParametersException("Session not found", 5);
        }
        if (!sessionOptional.get().getUser().getUuid().equals(request.uuid())) {
            return new ServerCheckController.JoinServerResponse(false);
        }
        var session = sessionOptional.get();
        session.setServerId(request.serverID);
        sessionService.save(session);
        return new ServerCheckController.JoinServerResponse(true);
    }


    @PostMapping("/checkserver")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public CheckServerResponse checkServer(@RequestBody CheckServerRequest request) {
        var userOptional = userService.findByUsername(request.username);
        if (userOptional.isEmpty()) {
            throw new InvalidParametersException("User not found", 1);
        }
        var sessionOptional = sessionService.findByUserAndServerId(userOptional.get(), request.serverID);
        if (sessionOptional.isEmpty()) {
            throw new InvalidParametersException("Session not found", 5);
        }
        var user = LauncherUserController.LauncherUser.fromDto(dtoService.toPublicUserDto(userOptional.get()));
        Map<String, String> properties = new HashMap<>();
        return new CheckServerResponse(user, Long.toString(sessionOptional.get().getHardwareId().getId()),
                Long.toString(sessionOptional.get().getId()),
                properties);
    }

    public record CheckServerRequest(String username, String serverID, boolean extended) {
    }

    public record JoinServerPublicRequest(String username, String accessToken, String serverID) {
    }

    public record JoinServerPublicUuidRequest(UUID uuid, String accessToken, String serverID) {
    }

    public record CheckServerResponse(LauncherUserController.LauncherUser user, String hardwareId, String sessionId,
                                      Map<String, String> sessionProperties) {
    }
}
