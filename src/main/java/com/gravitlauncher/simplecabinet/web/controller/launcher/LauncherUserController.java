package com.gravitlauncher.simplecabinet.web.controller.launcher;

import com.gravitlauncher.simplecabinet.web.dto.user.UserDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/launcher/user")
public class LauncherUserController {
    @Autowired
    private UserService service;
    @Autowired
    private DtoService dtoService;

    @GetMapping("/by/username/{name}")
    public LauncherUser getByUsername(@PathVariable String name, @RequestParam(defaultValue = "true") boolean assets) {
        var optional = assets ? service.findByUsernameFetchAssets(name) : service.findByUsername(name);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        return LauncherUser.fromDto(dtoService.toPublicUserDto(optional.get()));
    }

    @GetMapping("/by/uuid/{uuid}")
    public LauncherUser getByUUID(@PathVariable UUID uuid, @RequestParam(defaultValue = "true") boolean assets) {
        var optional = assets ? service.findByUuidFetchAssets(uuid) : service.findByUUID(uuid);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        return LauncherUser.fromDto(dtoService.toPublicUserDto(optional.get()));
    }

    public record LauncherTexture(String url, String digest, Map<String, String> metadata) {
        static LauncherTexture fromDto(UserDto.UserTexture texture) {
            return new LauncherTexture(texture.url,
                    Base64.getEncoder().encodeToString(HexFormat.of().parseHex(texture.digest)),
                    texture.metadata);
        }
    }

    public record LauncherUser(String username, UUID uuid, Map<String, LauncherTexture> assets,
                               Map<String, String> properties) {
        static LauncherUser fromDto(UserDto dto) {
            Map<String, LauncherTexture> assets = new HashMap<>();
            for (var e : dto.assets.entrySet()) {
                assets.put(e.getKey(), LauncherTexture.fromDto(e.getValue()));
            }
            return new LauncherUser(dto.username, dto.uuid, assets, new HashMap<>());
        }
    }
}
