package com.gravitlauncher.simplecabinet.web.controller;

import com.gravitlauncher.simplecabinet.web.WebApplication;
import com.gravitlauncher.simplecabinet.web.service.KeyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/status")
public class StatusController {
    @Autowired
    private KeyManagementService keyManagementService;

    @GetMapping("/publicinfo")
    public PublicStatusInfo getPublicInfo() {
        return new PublicStatusInfo(WebApplication.VERSION, Base64.getEncoder().encodeToString(keyManagementService.getEncodedPublicKey()));
    }

    public record PublicStatusInfo(String version, String jwtPublicKey) {
    }
}
