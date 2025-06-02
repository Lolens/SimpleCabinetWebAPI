package com.gravitlauncher.simplecabinet.web.controller.cabinet;

import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.service.user.PasswordCheckService;
import com.gravitlauncher.simplecabinet.web.service.user.SessionService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cabinet/security")
public class CabinetSecurityController {
    @Autowired
    private PasswordCheckService passwordCheckService;
    @Autowired
    private UserService userService;
    @Autowired
    private SessionService sessionService;

    @PostMapping("/changepassword")
    @Transactional
    public void changePassword(@RequestBody ChangePasswordRequest request) {
        var user = userService.getCurrentUser();
        var ref = user.getReference();
        if (!passwordCheckService.checkPassword(ref, request.oldPassword)) {
            throw new InvalidParametersException("Old password wrong", 10);
        }
        passwordCheckService.setPassword(ref, request.newPassword);
        sessionService.deactivateAllByUserWithExclude(ref, user.getSessionId());
        userService.save(ref);
    }

    @GetMapping("/info")
    public SecurityInfo getInfo() {
        var user = userService.getCurrentUser().getReference();
        return new SecurityInfo(user.getTotpSecretKey() != null);
    }

    @PostMapping("/prepare2fa")
    @Transactional
    public Prepare2FAResponse prepare2FA() {
        var user = userService.getCurrentUser().getReference();
        if (user.getTotpSecretKey() != null) {
            throw new InvalidParametersException("2FA already enabled", 17);
        }
        var secret = passwordCheckService.newTotpSecret();
        return new Prepare2FAResponse(secret, passwordCheckService.makeQrCodeUri(user, secret));
    }

    @PostMapping("/enable2fa")
    @Transactional
    public void enable2FA(@RequestBody Enable2FARequest request) {
        var user = userService.getCurrentUser().getReference();
        if (user.getTotpSecretKey() != null) {
            throw new InvalidParametersException("2FA already enabled", 17);
        }
        var secret = request.secret;
        if (!passwordCheckService.checkTotpPassword(secret, request.code)) {
            throw new InvalidParametersException("Code incorrect", 18);
        }
        user.setTotpSecretKey(secret);
        userService.save(user);
    }

    @PostMapping("/disable2fa")
    @Transactional
    public void disable2FA(@RequestBody Disable2FARequest request) {
        var user = userService.getCurrentUser().getReference();
        if (user.getTotpSecretKey() == null) {
            throw new InvalidParametersException("2FA already disabled", 19);
        }
        if (!passwordCheckService.checkTotpPassword(user, request.code)) {
            throw new InvalidParametersException("Code incorrect", 18);
        }
        user.setTotpSecretKey(null);
        userService.save(user);
    }

    public record ChangePasswordRequest(String oldPassword, String newPassword) {

    }

    public record Enable2FARequest(String secret, String code) {

    }

    public record Disable2FARequest(String code) {

    }

    public record Prepare2FAResponse(String secret, String uri) {

    }

    public record SecurityInfo(boolean enabled2FA) {

    }
}
