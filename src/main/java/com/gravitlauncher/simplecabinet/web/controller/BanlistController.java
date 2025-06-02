package com.gravitlauncher.simplecabinet.web.controller;

import com.gravitlauncher.simplecabinet.web.dto.BanInfoDto;
import com.gravitlauncher.simplecabinet.web.dto.PageDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.service.BanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/banlist")
public class BanlistController {
    @Autowired
    private BanService banService;

    @GetMapping("/userId/{userId}")
    public BanInfoDto getById(@PathVariable long userId) {
        var optional = banService.findById(userId);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("BanInfo not found");
        }
        return new BanInfoDto(optional.get());
    }

    @GetMapping("/page/{pageId}")
    public PageDto<BanInfoDto> getPage(@PathVariable int pageId) {
        var list = banService.findAll(PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(BanInfoDto::new));
    }
}
