package com.gravitlauncher.simplecabinet.web.service.updates;

import com.gravitlauncher.simplecabinet.web.model.updates.LauncherArtifact;
import com.gravitlauncher.simplecabinet.web.repository.update.LauncherArtifactRepository;
import com.gravitlauncher.simplecabinet.web.service.KeyManagementService;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

@Service
public class LauncherArtifactService {
    private LauncherArtifactRepository repository;
    private KeyManagementService keyManagementService;
    private JwtParser parser;

    public LauncherArtifactService(LauncherArtifactRepository repository, KeyManagementService keyManagementService) {
        this.repository = repository;
        this.keyManagementService = keyManagementService;
        this.parser = Jwts.parser()
                .requireIssuer("SimpleCabinet.LauncherArtifact")
                .verifyWith(keyManagementService.getPublicKey())
                .build();
    }

    @SneakyThrows
    public String makeJwtTokenForUpdate(String data) {
        LocalDateTime dateTime = LocalDateTime.now().plusMinutes(5);
        return Jwts.builder()
                .subject("UnknownUser")
                .issuer("SimpleCabinet.LauncherArtifact")
                .claim("data", data)
                .expiration(Date.from(dateTime.toInstant(ZoneOffset.UTC)))
                .signWith(keyManagementService.getPrivateKey(), Jwts.SIG.ES256).compact();
    }

    public String verifyJwtTokemForUpdate(String token) {
        var payload = parser.parseSignedClaims(token).getPayload();
        return payload.get("data", String.class);
    }

    public Optional<LauncherArtifact> findByPublicKey(byte[] publicKey) {
        return repository.findByPublicKey(publicKey);
    }

    public Optional<LauncherArtifact> findLatestRelease(String variant) {
        return repository.findLatestRelease(variant);
    }

    public void markAllOldArtifactIsDeprecated(String variant, Long latestReleaseId) {
        repository.markAllOldArtifactIsDeprecated(variant, latestReleaseId);
    }

    public <S extends LauncherArtifact> S save(S entity) {
        return repository.save(entity);
    }

    public Optional<LauncherArtifact> findById(Long aLong) {
        return repository.findById(aLong);
    }
}
