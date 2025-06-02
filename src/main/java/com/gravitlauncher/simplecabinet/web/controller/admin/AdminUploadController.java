package com.gravitlauncher.simplecabinet.web.controller.admin;

import com.gravitlauncher.simplecabinet.web.controller.cabinet.CabinetController;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.service.storage.StorageService;
import com.gravitlauncher.simplecabinet.web.service.user.UserAssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@RestController
@RequestMapping("/admin/upload")
public class AdminUploadController {
    private static final Logger logger = LoggerFactory.getLogger(CabinetController.class);
    @Autowired
    public UserAssetService userAssetService;
    @Autowired
    public StorageService storageService;

    @PostMapping("/simpleupload")
    public UploadedUrl uploadAsset(@RequestPart("file") MultipartFile file) {
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new InvalidParametersException("File upload failure", 21);
        }
        String hash = userAssetService.calculateHash(bytes);
        try {
            URL url = storageService.put(hash, bytes);
            return new UploadedUrl(url.toString(), hash);
        } catch (StorageService.StorageException e) {
            logger.error("StorageService.put failed", e);
            throw new InvalidParametersException("File upload failure", 22);
        }
    }

    public record UploadedUrl(String url, String name) {

    }
}
