package com.gravitlauncher.simplecabinet.web.controller.admin;

import com.gravitlauncher.simplecabinet.web.dto.BanInfoDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.service.BanService;
import com.gravitlauncher.simplecabinet.web.service.user.HardwareIdService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/moderation")
public class AdminModerationController {
    @Autowired
    private UserService userService;
    @Autowired
    private BanService banService;
    @Autowired
    private HardwareIdService hardwareIdService;

    @PostMapping("/ban/{userId}")
    public BanInfoDto banUser(@PathVariable long userId, @RequestBody BanRequest request) {
        var user = userService.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        if (request.endDate != null && !request.endDate.isAfter(LocalDateTime.now())) {
            throw new InvalidParametersException("endDate is not after current date", 32);
        }
        {
            var banInfo = banService.findBanByUser(user.get());
            if (banInfo.isPresent()) {
                throw new InvalidParametersException("User already banned", 33);
            }
        }
        var moderator = userService.getCurrentUser();
        var banInfo = banService.ban(user.get(), moderator.getReference(), request.reason, request.endDate);
        if (request.isHardware) {
            hardwareIdService.banByUser(user.get().getId());
        }
        userService.deactivateSessionsByUser(user.get());
        return new BanInfoDto(banInfo);
    }

    @PostMapping("/unban/{userId}")
    public void unbanUser(@PathVariable long userId) {
        var user = userService.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        var banInfo = banService.findBanByUser(user.get());
        if (banInfo.isEmpty()) {
            throw new InvalidParametersException("User not banned", 5);
        }
        banService.unban(banInfo.get());
        hardwareIdService.unbanByUser(user.get().getId());
    }

    public record BanRequest(String reason, LocalDateTime endDate, boolean isHardware) {
    }
}
