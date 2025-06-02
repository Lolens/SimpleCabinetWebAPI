package com.gravitlauncher.simplecabinet.web.controller.admin;

import com.gravitlauncher.simplecabinet.web.configuration.jwt.JwtProvider;
import com.gravitlauncher.simplecabinet.web.controller.AuthController;
import com.gravitlauncher.simplecabinet.web.dto.PageDto;
import com.gravitlauncher.simplecabinet.web.dto.user.UserSessionDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.model.user.UserSession;
import com.gravitlauncher.simplecabinet.web.service.user.HardwareIdService;
import com.gravitlauncher.simplecabinet.web.service.user.SessionService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/session")
public class AdminSessionController {
    @Autowired
    private SessionService sessionService;
    @Autowired
    private UserService userService;
    @Autowired
    private HardwareIdService hardwareIdService;
    @Autowired
    private JwtProvider jwtProvider;

    @GetMapping("/id/{id}")
    public UserSessionDto getById(@PathVariable long id) {
        var optional = sessionService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("UserSession not found");
        }
        return new UserSessionDto(optional.get(), true);
    }

    @PostMapping("/id/{id}/deactivate")
    public void deactivateById(@PathVariable long id) {
        var optional = sessionService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("UserSession not found");
        }
        var session = optional.get();
        session.setDeleted(true);
        sessionService.save(session);
    }

    @PostMapping("/id/{id}/sethardware")
    public void setHardwareById(@PathVariable long id, @RequestBody SessionUpdateHardwareRequest request) {
        var optional = sessionService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("UserSession not found");
        }
        var session = optional.get();
        var hardwareId = hardwareIdService.getById(request.id);
        session.setHardwareId(hardwareId);
        sessionService.save(session);
    }

    @DeleteMapping("/id/{id}")
    public void deleteById(@PathVariable long id) {
        var optional = sessionService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("UserSession not found");
        }
        var session = optional.get();
        sessionService.delete(session);
    }

    @GetMapping("/usersession/{userId}/page/{pageId}")
    public PageDto<UserSessionDto> getPageByUser(@PathVariable long userId, @PathVariable int pageId) {
        var user = userService.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        var list = sessionService.findByUser(user.get(), PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(x -> new UserSessionDto(x, true)));
    }

    @GetMapping("/serverid/{serverId}")
    public UserSessionDto getByServerId(@PathVariable String serverId) {
        var optional = sessionService.findByServerId(serverId);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("UserSession not found");
        }
        return new UserSessionDto(optional.get(), true);
    }

    @PostMapping("/usersession/{userId}/deactivateall")
    public void exitAllByUser(@PathVariable long userId) {
        var user = userService.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        sessionService.deactivateAllByUser(user.get());
    }

    @PostMapping("/sudo")
    public AuthController.AuthResponse sudo(@RequestBody CreateSudoSessionRequest request, HttpServletRequest servletRequest) {
        var user = userService.findById(request.userId());
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        if (request.shadow()) {
            UserSession userSession = new UserSession();
            userSession.setRefreshToken("SUDO_TOKEN");
            userSession.setClient(request.client());
            userSession.setUser(user.get());
            var token = jwtProvider.generateToken(userSession);
            return new AuthController.AuthResponse(token.token(), userSession.getRefreshToken(), token.getExpire());
        } else {
            var session = sessionService.create(user.get(), request.client(), servletRequest.getRemoteAddr());
            var token = jwtProvider.generateToken(session);
            return new AuthController.AuthResponse(token.token(), session.getRefreshToken(), token.getExpire());
        }
    }

    public record SessionUpdateHardwareRequest(long id) {
    }

    public record CreateSudoSessionRequest(long userId, String client, boolean shadow) {

    }
}
