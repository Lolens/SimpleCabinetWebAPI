package com.gravitlauncher.simplecabinet.web;

import com.gravitlauncher.simplecabinet.web.configuration.jwt.JwtProvider;
import com.gravitlauncher.simplecabinet.web.controller.AuthController;
import com.gravitlauncher.simplecabinet.web.controller.SetupController;
import com.gravitlauncher.simplecabinet.web.controller.UserController;
import com.gravitlauncher.simplecabinet.web.controller.admin.AdminModerationController;
import com.gravitlauncher.simplecabinet.web.controller.admin.AdminMoneyController;
import com.gravitlauncher.simplecabinet.web.controller.cabinet.CabinetController;
import com.gravitlauncher.simplecabinet.web.controller.cabinet.CabinetMoneyController;
import com.gravitlauncher.simplecabinet.web.controller.cabinet.CabinetSecurityController;
import com.gravitlauncher.simplecabinet.web.dto.user.UserDto;
import com.gravitlauncher.simplecabinet.web.security.WithCabinetUser;
import com.gravitlauncher.simplecabinet.web.service.user.PasswordCheckService;
import com.gravitlauncher.simplecabinet.web.service.user.UserDetailsService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootTest
public class UserTests {
    private static long basicUserId;
    private static String adminToken;
    private static String adminPassword;
    @Autowired
    private UserController userController;
    @Autowired
    private AuthController authController;
    @Autowired
    private CabinetController cabinetController;
    @Autowired
    private CabinetSecurityController cabinetSecurityController;
    @Autowired
    private AdminModerationController adminModerationController;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private CabinetMoneyController cabinetMoneyController;
    @Autowired
    private AdminMoneyController adminMoneyController;
    @Autowired
    private PasswordCheckService passwordCheckService;
    @Autowired
    private JwtProvider jwtProvider;

    @BeforeAll
    public static void prepare(@Autowired AuthController authController, @Autowired SetupController setupController) throws Exception {
        var setup = setupController.setup();
        adminToken = setup.accessToken();
        adminPassword = setup.password();
        var result = authController.register(new AuthController.RegisterRequest("test", "test@example.com", "test123", null));
        Assertions.assertNotNull(result);
        var id = result.id();
        Assertions.assertTrue(id > 0);
        basicUserId = id;
    }

    @Test
    @Transactional
    public void testAuthorization() throws Exception {
        var result = authController.auth(new AuthController.AuthRequest("test", "test123", null), null);
        Assertions.assertTrue(result.getStatusCode().is2xxSuccessful());
        Assertions.assertTrue(result.getHeaders().containsKey("Set-Cookie"));
        var body = result.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertNotNull(body.accessToken());
        Assertions.assertNotNull(body.refreshToken());
        Assertions.assertTrue(jwtProvider.validateToken(body.accessToken()));
        var refreshBody = authController.refreshToken(new AuthController.RefreshTokenRequest(body.refreshToken()));
        Assertions.assertNotNull(refreshBody);
        Assertions.assertNotNull(refreshBody.accessToken());
        Assertions.assertNotNull(refreshBody.refreshToken());
        Assertions.assertTrue(jwtProvider.validateToken(refreshBody.accessToken()));
        Assertions.assertNotEquals(body.accessToken(), refreshBody.accessToken());
        Assertions.assertNotEquals(body.refreshToken(), refreshBody.refreshToken());
    }

    @Test
    @WithCabinetUser(userId = 1)
    @Transactional
    public void testBasicCabinet() {
        {
            cabinetController.setStatus(new CabinetController.SetStatusRequest("Status"));
            var user = userService.getCurrentUser();
            Assertions.assertEquals(user.getReference().getStatus(), "Status");
        }
        {
            cabinetSecurityController.changePassword(new CabinetSecurityController.ChangePasswordRequest(adminPassword, "1111"));
            var user = userService.getCurrentUser();
            Assertions.assertTrue(passwordCheckService.checkPassword(user.getReference(), "1111"));
            Assertions.assertFalse(passwordCheckService.checkPassword(user.getReference(), "2222"));
        }
    }

    @Test
    @WithCabinetUser(userId = 1)
    @Transactional
    public void testBan() {
        {
            var result = authController.register(new AuthController.RegisterRequest("testBan", "testBan@example.com", "test123", null));
            adminModerationController.banUser(result.id(), new AdminModerationController.BanRequest("Test Reason", LocalDateTime.now().plus(Duration.ofHours(1)), false));
            try {
                authController.auth(new AuthController.AuthRequest("testBan", "test123", null), null);
                Assertions.fail("Success auth with banned user");
            } catch (Exception ignored) {

            }
            adminModerationController.unbanUser(result.id());
            authController.auth(new AuthController.AuthRequest("testBan", "test123", null), null);
        }
    }

    @Test
    @Transactional
    public void testRoles() {
        var result = userDetailsService.collectUserRoles(userService.getReference(1L));
        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(result.getFirst(), "ADMIN");
    }

    @Test
    @WithCabinetUser(userId = 1)
    @Transactional
    public void testBalance() {

    }

    @Test
    @Transactional
    public void testGetUser() throws Exception {
        var result = userController.getById(basicUserId, true);
        testUserDto(result);
        result = userController.getByUsername(result.username, true);
        testUserDto(result);
        result = userController.getByUUID(result.uuid, true);
        testUserDto(result);
    }

    private void testUserDto(UserDto userDto) {
        Assertions.assertNotNull(userDto);
        Assertions.assertNotNull(userDto.username);
        Assertions.assertNotNull(userDto.uuid);
        Assertions.assertNotNull(userDto.registrationDate);
    }
}
