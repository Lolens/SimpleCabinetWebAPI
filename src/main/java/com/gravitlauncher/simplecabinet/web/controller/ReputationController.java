package com.gravitlauncher.simplecabinet.web.controller;

import com.gravitlauncher.simplecabinet.web.dto.user.UserReputationChangeDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.model.user.UserReputationChange;
import com.gravitlauncher.simplecabinet.web.service.ReputationService;
import com.gravitlauncher.simplecabinet.web.service.user.UserDetailsService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import com.gravitlauncher.simplecabinet.web.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/reputation")
public class ReputationController {
    @Autowired
    private ReputationService reputationService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/rep/{userId}")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public UserReputationChangeDto rep(@PathVariable Long userId) {
        var user = SecurityUtils.getUser();
        var target = userService.findById(userId);
        if (target.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        var value = Long.parseLong(user.getPermission("reputation.value.add", "1"));
        var delay = Long.parseLong(user.getPermission("reputation.delay.add", "300"));
        if (!reputationService.checkDuration(user.getUserId(), userId, delay)) {
            throw new InvalidParametersException("Too frequently", 74);
        }
        var entity = reputationService.change(user.getUserId(), userId, value, UserReputationChange.ReputationChangeReason.NORMAL);
        return new UserReputationChangeDto(entity);
    }

    @PostMapping("/userrep/{userId}/to/{targetId}")
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UserReputationChangeDto userRep(@PathVariable Long userId, @PathVariable Long targetId, @RequestBody UserReputationRequest request) {
        var user = userService.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        var target = userService.findById(targetId);
        if (target.isEmpty()) {
            throw new EntityNotFoundException("Target user not found");
        }
        var permissions = userDetailsService.getUserPermissions(user.get());
        var value = Long.parseLong(Optional.ofNullable(permissions.get("reputation.value." + (request.isPlus() ? "add" : "remove"))).orElse("1"));
        var delay = Long.parseLong(Optional.ofNullable(permissions.get("reputation.delay." + (request.isPlus() ? "add" : "remove"))).orElse("300"));
        if (request.checkLimits() && !reputationService.checkDuration(user.get().getId(), userId, delay)) {
            throw new InvalidParametersException("Too frequently", 74);
        }
        var entity = reputationService.change(user.get().getId(), userId, value, request.reason());
        return new UserReputationChangeDto(entity);
    }

    @PostMapping("/setrep/{userId}")
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UserReputationChangeDto setRep(@PathVariable Long userId, @RequestBody SetReputationRequest request) {
        var user = SecurityUtils.getUser();
        var target = userService.findById(userId);
        if (target.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        var entity = reputationService.change(user.getUserId(), userId, request.value() - target.get().getReputation(), UserReputationChange.ReputationChangeReason.SET);
        return new UserReputationChangeDto(entity);
    }

    @PostMapping("/unrep/{userId}")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public UserReputationChangeDto unrep(@PathVariable Long userId) {
        var user = SecurityUtils.getUser();
        var target = userService.findById(userId);
        if (target.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        var value = Long.parseLong(user.getPermission("reputation.value.remove", "1"));
        var delay = Long.parseLong(user.getPermission("reputation.delay.remove", "300"));
        if (!reputationService.checkDuration(user.getUserId(), userId, delay)) {
            throw new InvalidParametersException("Too frequently", 74);
        }
        var entity = reputationService.change(user.getUserId(), userId, -value, UserReputationChange.ReputationChangeReason.NORMAL);
        return new UserReputationChangeDto(entity);
    }

    public record SetReputationRequest(Long value) {

    }

    public record UserReputationRequest(UserReputationChange.ReputationChangeReason reason, boolean isPlus,
                                        boolean checkLimits) {

    }
}
