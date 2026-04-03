package com.gravitlauncher.simplecabinet.web.controller.launcher;

import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.model.updates.LauncherArtifact;
import com.gravitlauncher.simplecabinet.web.service.storage.StorageService;
import com.gravitlauncher.simplecabinet.web.service.updates.LauncherArtifactService;
import com.gravitlauncher.simplecabinet.web.service.user.UserAssetService;
import com.gravitlauncher.simplecabinet.web.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/launcher/updates")
public class LauncherUpdatesController {


    @Autowired
    public UserAssetService userAssetService;
    @Autowired
    private LauncherArtifactService artifactService;
    @Autowired
    private StorageService storageService;

    @GetMapping("/prepare")
    public HttpUpdatesPrepare prepare() {
        var data = Base64.getEncoder().encodeToString(SecurityUtils.generateRandomString(16).getBytes(StandardCharsets.UTF_8));
        return new HttpUpdatesPrepare(data, artifactService.makeJwtTokenForUpdate(data));
    }

    @PostMapping("/check/{variant}")
    public LauncherUpdateInfo check(@PathVariable String variant, @RequestBody HttpUpdatesCheck request) {
        byte[] decodedPublicKey = Base64.getDecoder().decode(request.publicKey);
        var launcherArtifact = artifactService.findByPublicKey(decodedPublicKey);
        if (launcherArtifact.isEmpty()) {
            log.warn("LauncherArtifact with public key {} not found", request.publicKey);
            return makeRequiredUpdate(variant);
        }
        if (launcherArtifact.get().isDeprecated()) {
            log.info("LauncherArtifact {} ({}) found, but deprecated", launcherArtifact.get().getId(), launcherArtifact.get().getArtifactType());
            return makeRequiredUpdate(variant);
        }
        boolean verified;
        try {
            var data = artifactService.verifyJwtTokenForUpdate(request.jwtToken);
            KeyFactory fact = KeyFactory.getInstance("EC");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedPublicKey);
            ECPublicKey publicKey = (ECPublicKey) fact.generatePublic(keySpec);
            Signature sig = Signature.getInstance("SHA256withECDSA");
            sig.initVerify(publicKey);
            sig.update(Base64.getDecoder().decode(data));
            verified = sig.verify(Base64.getDecoder().decode(request.signedData));
        } catch (Throwable e) {
            log.error("Failed to verify update", e);
            verified = false;
        }
        if (!verified) {
            log.info("LauncherArtifact {} ({}) found, but signature not valid", launcherArtifact.get().getId(), launcherArtifact.get().getArtifactType());
            return makeRequiredUpdate(variant);
        }
        return new LauncherUpdateInfo(null, "1.0.0", false, false,
                artifactService.makeJwtTokenForverify(variant));
    }

    private LauncherUpdateInfo makeRequiredUpdate(String type) {
        var latestUpdate = artifactService.findLatestRelease(type);
        if (latestUpdate.isEmpty()) {
            log.warn("LauncherArtifact with type {} not found", type);
            return new LauncherUpdateInfo(null, "1.0.0", false, false, null);
        }
        return new LauncherUpdateInfo(
                storageService.getUrl(latestUpdate.get().getArtifactId()).toString(),
                "1.0.0",
                true,
                true,
                null
        );
    }

    @PostMapping("/upload/{variant}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public LauncherArtifactUploaded uploadUpdate(@PathVariable String variant, @RequestPart("secrets") HttpBuildSecrets secrets, @RequestPart("file") MultipartFile file) {
        {
            byte[] bytes;
            try {
                bytes = file.getBytes();
            } catch (IOException e) {
                throw new InvalidParametersException("File upload failure", 21);
            }
            String hash = userAssetService.calculateHash(bytes);
            URL url;
            try {
                url = storageService.put(hash, bytes);
            } catch (StorageService.StorageException e) {
                log.error("StorageService.put failed", e);
                throw new InvalidParametersException("File upload failure", 22);
            }
            LauncherArtifact launcherArtifact = new LauncherArtifact();
            launcherArtifact.setArtifactId(hash);
            launcherArtifact.setUploadAt(LocalDateTime.now());
            launcherArtifact.setPublicKey(Base64.getDecoder().decode(secrets.publicKey()));
            launcherArtifact.setDeprecated(false);
            launcherArtifact.setArtifactType(variant);
            launcherArtifact = artifactService.save(launcherArtifact);
            artifactService.markAllOldArtifactIsDeprecated(variant, launcherArtifact.getId());
            return new LauncherArtifactUploaded(storageService.getUrl(hash).toString());
        }
    }

    public record LauncherArtifactUploaded(String url) {

    }

    public record LauncherUpdateInfo(String url, String version, boolean available, boolean required, String jwtToken) {
    }

    public record HttpUpdatesCheck(String signedData, String publicKey, String jwtToken) {
    }

    public record HttpUpdatesPrepare(String data, String jwtToken) {
    }

    public record HttpBuildSecrets(String publicKey) {

    }
}
