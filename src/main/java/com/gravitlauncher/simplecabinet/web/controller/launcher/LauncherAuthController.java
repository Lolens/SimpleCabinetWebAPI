package com.gravitlauncher.simplecabinet.web.controller.launcher;

import com.gravitlauncher.simplecabinet.web.configuration.jwt.JwtProvider;
import com.gravitlauncher.simplecabinet.web.controller.AuthController;
import com.gravitlauncher.simplecabinet.web.exception.AuthException;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.service.BanService;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.updates.LauncherArtifactService;
import com.gravitlauncher.simplecabinet.web.service.user.PasswordCheckService;
import com.gravitlauncher.simplecabinet.web.service.user.SessionService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import com.gravitlauncher.simplecabinet.web.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/launcher/auth")
public class LauncherAuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private BanService banService;
    @Autowired
    private PasswordCheckService passwordCheckService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private DtoService dtoService;
    @Autowired
    private LauncherArtifactService artifactService;

    @PostMapping("/authorize")
    public ResponseEntity<AuthController.AuthResponse> auth(@RequestBody AuthRequest request, HttpServletRequest servletRequest) {
        var optional = userService.findByUsernameOrEmailWithGroups(request.login);
        if (optional.isEmpty()) {
            throw new AuthException("User not found", 3);
        }
        var user = optional.get();
        var banInfo = banService.findBanByUser(user);
        if (banInfo.isPresent()) {
            var info = banInfo.get();
            throw new AuthException(String.format("You banned: %s expired %s", info.getReason(), info.getEndAt() == null ? "never" : info.getEndAt().toString()), 4);
        }
        var success = passwordCheckService.checkPassword(user, request.password);
        if (!success) {
            throw new AuthException("Password not correct", 5);
        }
        if (user.getTotpSecretKey() != null) {
            if (request.totp == null) {
                throw new AuthException("auth.require2fa", 7);
            }
            if (!passwordCheckService.checkTotpPassword(user, request.totp)) {
                throw new AuthException("2FA Password not correct", 6);
            }
        }
        var launcherVerifyToken = servletRequest.getHeader("X-Launcher-Update-Token");
        String client = "Basic";
        if (launcherVerifyToken != null) {
            client = "LAUNCHER:" + artifactService.verifyJwtTokenForVerify(launcherVerifyToken);
        }
        var session = sessionService.create(user, client, servletRequest.getRemoteAddr());
        var token = jwtProvider.generateToken(session);
        HttpCookie cookie = ResponseCookie.from("session", token.token())
                .path("/")
                .sameSite("Strict")
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", cookie.toString());
        return new ResponseEntity<>(new AuthController.AuthResponse(token.token(), session.getRefreshToken(), token.getExpire()), headers, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public AuthController.AuthResponse refreshToken(@RequestBody RefreshTokenRequest request, HttpServletRequest servletRequest) {
        var sessionOptional = sessionService.updateRefreshToken(request.refreshToken);
        if (sessionOptional.isEmpty()) {
            throw new AuthException("Invalid refreshToken", 8);
        }

        var launcherVerifyToken = servletRequest.getHeader("X-Launcher-Update-Token");
        String client = "Basic";
        if (launcherVerifyToken != null) {
            client = "LAUNCHER:" + artifactService.verifyJwtTokenForVerify(launcherVerifyToken);
        }
        var session = sessionOptional.get();
        if (session.getClient().startsWith("LAUNCHER:") && launcherVerifyToken == null) {
            throw new AuthException("This token make for Launcher, but X-Update-Verify-Token not provided");
        }
        var token = jwtProvider.generateToken(session);
        return new AuthController.AuthResponse(token.token(), session.getRefreshToken(), token.getExpire());
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout() {
        var details = SecurityUtils.getUser();
        if (!sessionService.deactivateById(details.getSessionId())) {
            throw new AuthException("Invalid session", 9);
        }
        HttpCookie cookie = ResponseCookie.from("session", "deleted")
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", cookie.toString());
        return ResponseEntity.ok().headers(headers).build();
    }

    @GetMapping("/currentuser")
    @PreAuthorize("isAuthenticated()")
    public LauncherUserController.LauncherUser getUserInfo() {
        var details = SecurityUtils.getUser();
        var userOptional = userService.findByIdFetchAssets(details.getUserId());
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        return LauncherUserController.LauncherUser.fromDto(dtoService.toPrivateUserDto(userOptional.get()));
    }

    public record AuthRequest(String login, String password, String totp) {
    }

    public record RefreshTokenRequest(String refreshToken) {
    }
}
