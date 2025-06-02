package com.gravitlauncher.simplecabinet.web.configuration.jwt;

import com.gravitlauncher.simplecabinet.web.service.KeyManagementService;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtParserProvider {
    @Autowired
    private KeyManagementService service;

    public JwtParser makeParser() {
        return Jwts.parser()
                .requireIssuer("SimpleCabinet")
                .verifyWith(service.getPublicKey())
                .build();
    }
}
