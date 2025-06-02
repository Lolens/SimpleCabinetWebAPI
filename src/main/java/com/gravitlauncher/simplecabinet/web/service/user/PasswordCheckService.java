package com.gravitlauncher.simplecabinet.web.service.user;

import com.gravitlauncher.simplecabinet.web.model.user.BasicUser;
import com.gravitlauncher.simplecabinet.web.model.user.PrepareUser;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.repository.user.UserRepository;
import com.gravitlauncher.simplecabinet.web.utils.SecurityUtils;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordCheckService {
    private final PasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
    private final TimeProvider timeProvider = new SystemTimeProvider();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator();
    private final CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    private final SecretGenerator secretGenerator = new DefaultSecretGenerator(64);
    @Autowired
    private UserRepository repository;
    @Value("${cabinet.2fa.name}")
    private String qrCodeIssuerName;

    public boolean checkPassword(User user, String password) {
        if (password == null) {
            return false;
        }
        User.HashType type = user.getHashType();
        switch (type) {
            case BCRYPT -> {
                return bcryptEncoder.matches(password, user.getPassword());
            }
            case DOUBLEMD5 -> {
                return DigestUtils.md5Hex(DigestUtils.md5Hex(password)).equals(user.getPassword());
            }
            case MD5 -> {
                return DigestUtils.md5Hex(password).equals(user.getPassword());
            }
            case SHA256 -> {
                return DigestUtils.sha256Hex(password).equals(user.getPassword());
            }
            case AUTHMESHA256 -> {
                return SecurityUtils.verifyAuthMeSha256Password(password, user.getPassword());
            }
            case PHPASS -> {
            }
        }
        return false;
    }

    public boolean checkTotpPassword(User user, String totpPassword) {
        return verifier.isValidCode(user.getTotpSecretKey(), totpPassword);
    }

    public boolean checkTotpPassword(String secret, String totpPassword) {
        return verifier.isValidCode(secret, totpPassword);
    }

    public String makeQrCodeUri(BasicUser user, String totpSecret) {
        QrData data = new QrData.Builder()
                .label(user.getUsername())
                .secret(totpSecret)
                .issuer(qrCodeIssuerName)
                .algorithm(HashingAlgorithm.SHA1) // More on this below
                .digits(6)
                .period(30)
                .build();
        return data.getUri();
    }

    public String newTotpSecret() {
        return secretGenerator.generate();
    }

    public void setPassword(User user, String password) {
        user.setHashType(User.HashType.BCRYPT);
        user.setRawPassword(bcryptEncoder.encode(password));
    }

    public void setPassword(PrepareUser user, String password) {
        user.setHashType(User.HashType.BCRYPT);
        user.setPassword(bcryptEncoder.encode(password));
    }
}
