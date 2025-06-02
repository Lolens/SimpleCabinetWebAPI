package com.gravitlauncher.simplecabinet.web.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserAsset;
import com.gravitlauncher.simplecabinet.web.repository.user.UserAssetRepository;
import com.gravitlauncher.simplecabinet.web.service.storage.StorageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserAssetService {
    private static final AssetLimits DEFAULT_ASSET_LIMITS = new AssetLimits(64, 64, 32 * 1024);
    @Autowired
    private UserAssetRepository userAssetRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ObjectMapper objectMapper;
    private final List<String> allowedAssets = List.of("skin", "cape");

    public Optional<UserAsset> findById(Long aLong) {
        return userAssetRepository.findById(aLong);
    }

    public <S extends UserAsset> S save(S entity) {
        return userAssetRepository.save(entity);
    }

    public List<UserAsset> findAllByUser(User user) {
        return userAssetRepository.findAllByUser(user);
    }

    public Optional<UserAsset> findByUserAndName(User user, String name) {
        return userAssetRepository.findByUserAndName(user, name);
    }

    public void deleteById(Long aLong) {
        userAssetRepository.deleteById(aLong);
    }

    public void delete(UserAsset entity) {
        userAssetRepository.delete(entity);
    }

    public String makeAssetUrl(UserAsset userAsset) {
        return storageService.getUrl(userAsset.getHash()).toString();
    }


    private MessageDigest makeMessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(e);
        }
    }

    public String calculateHash(byte[] bytes) {
        MessageDigest messageDigest = makeMessageDigest();
        return String.valueOf(Hex.encode(messageDigest.digest(bytes)));
    }

    public AssetLimits getAssetLimits(String type, UserService.CurrentUser user) {
        var maxHeight = user.getPermission(String.format("upload.%s.height", type));
        var maxWidth = user.getPermission(String.format("upload.%s.width", type));
        var maxBytes = user.getPermission(String.format("upload.%s.bytes", type));
        return new AssetLimits(maxHeight == null ? DEFAULT_ASSET_LIMITS.maxHeight : Integer.parseInt(maxHeight),
                maxWidth == null ? DEFAULT_ASSET_LIMITS.maxWidth : Integer.parseInt(maxWidth),
                maxBytes == null ? DEFAULT_ASSET_LIMITS.maxBytes : Integer.parseInt(maxBytes));
    }

    public String createMetadata(String name, AssetOptions options) {
        Map<String, String> metadata = new HashMap<>();
        if ("skin".equals(name) && options.modelSlim) {
            metadata.put("model", "slim");
        }
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    public boolean checkLimitsPre(MultipartFile file, AssetLimits limits) {
        return file.getSize() <= limits.maxBytes();
    }

    public boolean checkLimitsPost(InputStream stream, AssetLimits limits) {
        try {
            BufferedImage image = ImageIO.read(stream);
            if (image.getHeight() > limits.maxHeight()) {
                return false;
            }
            if (image.getWidth() > limits.maxWidth()) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean isAllowed(String name) {
        return allowedAssets.contains(name);
    }

    public record AssetLimits(int maxHeight, int maxWidth, long maxBytes) {

    }

    public record AssetOptions(boolean modelSlim) {

    }
}
