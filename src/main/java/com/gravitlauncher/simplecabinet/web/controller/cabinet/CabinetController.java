package com.gravitlauncher.simplecabinet.web.controller.cabinet;

import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cabinet")
public class CabinetController {

    @Autowired
    private UserService userService;

    @PostMapping("/setstatus")
    public void setStatus(@RequestBody SetStatusRequest request) {
        var user = userService.getCurrentUser();
        var ref = user.getReference();
        ref.setStatus(request.status);
        userService.save(ref);
    }

    @PostMapping("/setgender")
    public void setGender(@RequestBody SetGenderRequest request) {
        var user = userService.getCurrentUser();
        var ref = user.getReference();
        ref.setGender(request.gender);
        userService.save(ref);
    }

    public record SetStatusRequest(String status) {
    }

    public record SetGenderRequest(User.Gender gender) {
    }
}