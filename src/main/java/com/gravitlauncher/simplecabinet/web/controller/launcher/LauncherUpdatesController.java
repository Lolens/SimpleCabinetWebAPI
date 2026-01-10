package com.gravitlauncher.simplecabinet.web.controller.launcher;

import com.gravitlauncher.simplecabinet.web.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/launcher/updates")
public class LauncherUpdatesController {
    @GetMapping("/prepare")
    public HttpUpdatesPrepare prepare() {
        return new HttpUpdatesPrepare(SecurityUtils.generateRandomString(16), "UNUSED_STUB_TOKEN");
    }

    @PostMapping("/check")
    public LauncherUpdateInfo check(@RequestBody HttpUpdatesCheck request) {
        return new LauncherUpdateInfo(null, "1.0.0", false, false);
    }

    public record LauncherUpdateInfo(String url, String version, boolean available, boolean required) {
    }

    public record HttpUpdatesCheck(String signedData, String publicKey, String jwtToken) {
    }

    public record HttpUpdatesPrepare(String data, String jwtToken) {
    }
}
