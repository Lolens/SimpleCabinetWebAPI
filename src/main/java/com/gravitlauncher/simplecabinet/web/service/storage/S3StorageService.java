package com.gravitlauncher.simplecabinet.web.service.storage;

import com.gravitlauncher.simplecabinet.web.configuration.properties.storage.S3StorageConfig;
import jakarta.annotation.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

@Service
@Priority(value = 0)
@ConditionalOnProperty(
        value = "storage.s3.enabled")
public class S3StorageService implements StorageService {

    private final S3StorageConfig config;
    private final S3Client client;

    @Autowired
    public S3StorageService(S3StorageConfig config) {
        this.config = config;
        client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())))
                .region(Region.of(config.getRegion()))
                .endpointOverride(URI.create(config.getEndpoint()))
                .forcePathStyle(true)
                .build();
    }

    @Override
    public URL put(String name, byte[] bytes) throws StorageException {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(config.getBucket())
                .key(name)
                .build();
        try {
            client.putObject(objectRequest, RequestBody.fromBytes(bytes));
        } catch (Exception e) {
            throw new StorageException(e);
        }
        return getUrl(name);
    }

    @Override
    public URL put(String name, InputStream stream, long length) throws StorageException {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(config.getBucket())
                .key(name)
                .build();
        try {
            client.putObject(objectRequest, RequestBody.fromInputStream(stream, length));
        } catch (Exception e) {
            throw new StorageException(e);
        }
        return getUrl(name);
    }

    @Override
    public URL getUrl(String name) {
        return client.utilities().getUrl(GetUrlRequest.builder()
                .bucket(config.getBucket())
                .key(name)
                .build());
    }
}
