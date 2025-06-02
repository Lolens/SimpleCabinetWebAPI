package com.gravitlauncher.simplecabinet.web.controller.cabinet;

import com.gravitlauncher.simplecabinet.web.dto.PageDto;
import com.gravitlauncher.simplecabinet.web.dto.user.UserSessionDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.service.user.SessionService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cabinet/sessions")
public class CabinetSessionsController {
    @Autowired
    private UserService userService;
    @Autowired
    private SessionService sessionService;

    @GetMapping("/page/{pageId}")
    public PageDto<UserSessionDto> getPage(@PathVariable int pageId) {
        var user = userService.getCurrentUser();
        var list = sessionService.findByUserPublic(user.getReference(), PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(UserSessionDto::new));
    }

    @GetMapping("/current")
    public UserSessionDto getCurrentSession() {
        var user = userService.getCurrentUser();
        var session = sessionService.findById(user.getSessionId());
        if (session.isEmpty()) {
            throw new EntityNotFoundException("Session not found");
        }
        return new UserSessionDto(session.get());
    }

    @GetMapping("/id/{id}")
    public UserSessionDto getById(@PathVariable long id) {
        var user = userService.getCurrentUser();
        var optional = sessionService.findById(id);
        if (optional.isEmpty() || optional.get().isDeleted()) {
            throw new EntityNotFoundException("Session not found");
        }
        var session = optional.get();
        if (session.getUser().getId() != user.getId()) {
            throw new SecurityException("Access denied");
        }
        return new UserSessionDto(session);
    }

    @DeleteMapping("/id/{id}")
    public void deleteById(@PathVariable long id) {
        var user = userService.getCurrentUser();
        var optional = sessionService.findById(id);
        if (optional.isEmpty() || optional.get().isDeleted()) {
            throw new EntityNotFoundException("Session not found");
        }
        var session = optional.get();
        if (session.getUser().getId() != user.getId()) {
            throw new SecurityException("Access denied");
        }
        if (session.getId() == user.getSessionId()) {
            throw new SecurityException("You can't delete current session");
        }
        session.setDeleted(true);
        sessionService.save(session);
    }
}
