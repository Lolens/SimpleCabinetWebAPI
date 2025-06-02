package com.gravitlauncher.simplecabinet.web.service;

import lombok.Getter;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

@Getter
public class KeyManagementService {
    private final ECPublicKey publicKey;
    private final ECPrivateKey privateKey;
    private final byte[] encodedPublicKey;

    public KeyManagementService(ECPublicKey publicKey, ECPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.encodedPublicKey = publicKey.getEncoded();
    }

}
