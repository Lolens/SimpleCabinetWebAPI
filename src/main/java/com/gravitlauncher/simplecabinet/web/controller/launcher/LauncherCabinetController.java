package com.gravitlauncher.simplecabinet.web.controller.launcher;

import com.gravitlauncher.simplecabinet.web.controller.cabinet.CabinetController;
import com.gravitlauncher.simplecabinet.web.dto.user.UserDto;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.model.user.UserAsset;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.storage.StorageService;
import com.gravitlauncher.simplecabinet.web.service.user.UserAssetService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/launcher/cabinet")
public class LauncherCabinetController {
    private static final Logger logger = LoggerFactory.getLogger(CabinetController.class);
    @Autowired
    public UserAssetService userAssetService;
    @Autowired
    public UserService userService;
    @Autowired
    public DtoService dtoService;
    @Autowired
    public StorageService storageService;

    @PostMapping("/upload/{name}")
    public UserDto.UserTexture uploadAsset(@PathVariable String name, @RequestPart("options") UserAssetService.AssetOptions options, @RequestPart("file") MultipartFile file) {
        var user = userService.getCurrentUser();
        name = name.toLowerCase();
        if (!userAssetService.isAllowed(name)) {
            throw new InvalidParametersException("Asset name not allowed", 20);
        }
        var limits = userAssetService.getAssetLimits(name, user);
        if (!userAssetService.checkLimitsPre(file, limits)) {
            throw new InvalidParametersException("File too large", 7);
        }
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new InvalidParametersException("File upload failure", 21);
        }
        if (!userAssetService.checkLimitsPost(new ByteArrayInputStream(bytes), limits)) {
            throw new InvalidParametersException("File height or width exceeds the limit", 8);
        }
        String hash = userAssetService.calculateHash(bytes);
        String metadata = userAssetService.createMetadata(name, options);
        var asset = userAssetService.findByUserAndName(user.getReference(), name);
        UserAsset newAsset;
        if (asset.isEmpty()) {
            newAsset = new UserAsset();
            newAsset.setUser(user.getReference());
        } else {
            newAsset = asset.get();
        }
        newAsset.setHash(hash);
        newAsset.setName(name);
        newAsset.setMetadata(metadata);
        try {
            storageService.put(hash, bytes);
        } catch (StorageService.StorageException e) {
            logger.error("StorageService.put failed", e);
            throw new InvalidParametersException("File upload failure", 22);
        }
        userAssetService.save(newAsset);
        return dtoService.getUserTexture(newAsset);
    }
}
