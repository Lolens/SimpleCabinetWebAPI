package com.gravitlauncher.simplecabinet.web.controller;

import com.gravitlauncher.simplecabinet.web.configuration.jwt.JwtProvider;
import com.gravitlauncher.simplecabinet.web.dto.user.UserDto;
import com.gravitlauncher.simplecabinet.web.exception.AuthException;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.service.BanService;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.RegisterService;
import com.gravitlauncher.simplecabinet.web.service.captcha.CaptchaService;
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
@RequestMapping("/auth")
public class AuthController {
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
    private RegisterService registerService;
    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        var isAdmin = SecurityUtils.checkAuthority("ROLE_ADMIN");
        if (!isAdmin && !captchaService.verify(request.captcha)) {
            throw new InvalidParametersException("Invalid captcha response", 36);
        }
        if (!isAdmin && !registerService.isEnabled()) {
            throw new InvalidParametersException("Registration disabled", 31);
        }
        registerService.check(request.username, request.email, request.password);
        var result = registerService.register(request.username, request.email, request.password);
        return new RegisterResponse(result.id(), result.prepared(), result.needConfirm()); // TODO
    }

    @PostMapping("/regconfirm")
    public void regConfirm(@RequestBody RegConfirm regConfirm) {
        var user = registerService.confirm(regConfirm.token);
        if (user.isEmpty()) {
            throw new AuthException("Confirm token not found", 19);
        }
    }

    @PostMapping("/authorize")
    public ResponseEntity<AuthResponse> auth(@RequestBody AuthRequest request, HttpServletRequest servletRequest) {
        var optional = userService.findByUsernameOrEmailWithGroups(request.username);
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
            if (request.totpPassword == null) {
                throw new AuthException("auth.require2fa", 7);
            }
            if (!passwordCheckService.checkTotpPassword(user, request.totpPassword)) {
                throw new AuthException("2FA Password not correct", 6);
            }
        }
        var session = sessionService.create(user, "Basic", servletRequest.getRemoteAddr());
        var token = jwtProvider.generateToken(session);
        HttpCookie cookie = ResponseCookie.from("session", token.token())
                .path("/")
                .sameSite("Strict")
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", cookie.toString());
        return new ResponseEntity<>(new AuthResponse(token.token(), session.getRefreshToken(), token.getExpire()), headers, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        var sessionOptional = sessionService.updateRefreshToken(request.refreshToken);
        if (sessionOptional.isEmpty()) {
            throw new AuthException("Invalid refreshToken", 8);
        }
        var session = sessionOptional.get();
        var token = jwtProvider.generateToken(session);
        return new AuthResponse(token.token(), session.getRefreshToken(), token.getExpire());
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

    @GetMapping("/userinfo")
    @PreAuthorize("isAuthenticated()")
    public UserDto getUserInfo() {
        var details = SecurityUtils.getUser();
        var userOptional = userService.findByIdFetchAssets(details.getUserId());
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        return dtoService.toPrivateUserDto(userOptional.get());
    }

    public record RegisterResponse(long id, boolean prepared, boolean needConfirm) {
    }

    public record RegisterRequest(String username, String email, String password, String captcha) {
    }

    public record AuthRequest(String username, String password, String totpPassword) {
    }

    public record AuthResponse(String accessToken, String refreshToken, long expire) {
    }

    public record RefreshTokenRequest(String refreshToken) {

    }

    public record RegConfirm(String token) {

    }
}
