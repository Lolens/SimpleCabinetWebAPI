package com.gravitlauncher.simplecabinet.web.controller.admin;

import com.gravitlauncher.simplecabinet.web.dto.PageDto;
import com.gravitlauncher.simplecabinet.web.dto.shop.ItemDeliveryDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.shop.item.ItemDeliveryService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/delivery")
public class AdminDeliveryController {
    @Autowired
    private ItemDeliveryService service;
    @Autowired
    private UserService userService;
    @Autowired
    private DtoService dtoService;

    @GetMapping("/user/{username}/{pageId}")
    public PageDto<ItemDeliveryDto> getAllByUser(@PathVariable String username, @PathVariable int pageId) {
        var user = userService.findByUsername(username);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        var list = service.findAllByUser(user.get(), PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(dtoService::itemDeliveryDto));
    }

    @PostMapping("/id/{id}/setpart")
    public void setPart(@PathVariable long id, @RequestBody SetPartRequest request) {
        var itemDelivery = service.findById(id);
        if (itemDelivery.isEmpty()) {
            throw new EntityNotFoundException("ItemDelivery not found");
        }
        var item = itemDelivery.get();
        service.setPart(item, request.deliveredPart);
    }

    public record SetPartRequest(long deliveredPart) {
    }
}
