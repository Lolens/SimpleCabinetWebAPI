package com.gravitlauncher.simplecabinet.web.utils;

import com.gravitlauncher.simplecabinet.web.exception.AuthException;
import com.gravitlauncher.simplecabinet.web.service.user.UserDetailsService;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class SecurityUtils {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0987654321";

    public static UserDetailsService.CabinetUserDetails tryUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsService.CabinetUserDetails)) {
            return null;
        }
        return (UserDetailsService.CabinetUserDetails) auth.getPrincipal();
    }

    public static UserDetailsService.CabinetUserDetails getUser() {
        var user = tryUser();
        if (user == null) {
            throw new AuthException("Session not found");
        }
        return user;
    }

    public static boolean checkAuthority(String name) {
        var user = tryUser();
        if (user == null) {
            return false;
        }
        return user.checkAuthority(name);
    }

    public static String generateRandomString(int length) {
        Random rng = new SecureRandom();
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = CHARACTERS.charAt(rng.nextInt(CHARACTERS.length()));
        }
        return new String(text);
    }


    public static String hmacSha256(String data, String key) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return Hex.encodeHexString(mac.doFinal(data.getBytes()));
    }

    public static boolean verifyAuthMeSha256Password(String password, String hashedPassword) {
        String[] splited = hashedPassword.split("\\$");
        if (splited.length != 4) {
            return false;
        }
        String salt = splited[2];
        String saltedHash = splited[3];
        String checkHash = DigestUtils.sha256Hex(DigestUtils.sha256Hex(password).concat(salt));
        return saltedHash.equals(checkHash);
    }
}
