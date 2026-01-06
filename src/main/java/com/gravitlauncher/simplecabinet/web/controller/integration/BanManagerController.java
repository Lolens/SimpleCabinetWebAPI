package com.gravitlauncher.simplecabinet.web.controller.integration;

import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/integration/banmanager")
public class BanManagerController {
    @Autowired
    private UserService service;
    @Autowired
    private DtoService dtoService;

    @GetMapping("/name/{name}")
    public BanManagerController.UserUUID getByUsername(@PathVariable String name) {
        var  findUuid = service.findByUsername(name);
        return dtoService.toUsernameUuid(findUuid.get());
    }

    @GetMapping("/uuid/{uuidString}")
    public BanManagerController.UserUUID getByUUID(@PathVariable String  uuidString) {
         var  findUuid = service.findByUUID(stringToUUID(uuidString));
        return dtoService.toUsernameUuid(findUuid.get());
    }

    private UUID stringToUUID (String uuid){
        return UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }


    public static class UserUUID {
        public final String uuid;
        public final String username;

        public UserUUID(String username,UUID uuid) {
            this.username = username;
            this.uuid= uuid.toString().replace("-", "");
        }
    }
}
