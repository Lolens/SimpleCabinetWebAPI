package com.gravitlauncher.simplecabinet.web.controller.shop;

import com.gravitlauncher.simplecabinet.web.dto.PageDto;
import com.gravitlauncher.simplecabinet.web.dto.shop.ServiceOrderDto;
import com.gravitlauncher.simplecabinet.web.dto.shop.ServiceProductDto;
import com.gravitlauncher.simplecabinet.web.exception.BalanceException;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.model.shop.ServiceProduct;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.shop.group.GroupSearchService;
import com.gravitlauncher.simplecabinet.web.service.shop.service.ServiceProductService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/shop/service/")
public class ServiceShopController {
    @Autowired
    private ServiceProductService serviceProductService;
    @Autowired
    private UserService userService;
    @Autowired
    private DtoService dtoService;
    @Autowired
    private GroupSearchService searchsr;

    @GetMapping("/page/{pageId}")
    public PageDto<ServiceProductDto> getPage(@PathVariable int pageId) {
        var list = serviceProductService.findAllAvailable(PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(dtoService::toServiceProductDto));
    }

    @GetMapping("/id/{id}")
    public ServiceProductDto getById(@PathVariable long id) {
        var optional = serviceProductService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("GroupProduct not found");
        }
        return dtoService.toServiceProductDto(optional.get());
    }

    @GetMapping("/type/{type}/{pageId}")
    public PageDto<ServiceProductDto> getByType(@PathVariable ServiceProduct.ServiceType type, @PathVariable int pageId) {
        var list = serviceProductService.findByType(type, PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(dtoService::toServiceProductDto));
    }

    @PostMapping("/id/{id}/updatepicture")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void updatePicture(@PathVariable long id, @RequestBody ServiceProductUpdatePictureRequest request) {
        var optional = serviceProductService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Group not found");
        }
        var product = optional.get();
        product.setPictureUrl(request.pictureName);
        serviceProductService.save(product);
    }

    @PostMapping("/buy")
    @PreAuthorize("isAuthenticated()")
    public ServiceOrderDto buyService(@RequestBody BuyServiceRequest request) {
        var product = serviceProductService.findById(request.id);
        if (product.isEmpty()) {
            throw new InvalidParametersException("Product not found", 1);
        }
        if (request.quantity <= 0) {
            throw new InvalidParametersException("quantity <= 0", 2);
        }
        var user = userService.getCurrentUser();
        if (!product.get().isStackable()) {
            var prevOrder = serviceProductService.findByUserAndType(user.getReference(), product.get().getType());
            if (prevOrder.isPresent()) {
                throw new BalanceException("You have already purchased the same type of product", 25);
            }
        }
        var order = serviceProductService.createServiceOrder(product.get(), request.quantity, user.getReference());
        return new ServiceOrderDto(order);
    }

    @GetMapping("/type/{type}/order")
    @PreAuthorize("isAuthenticated()")
    public ServiceOrderDto getServiceOrder(@PathVariable ServiceProduct.ServiceType type) {
        var user = userService.getCurrentUser();
        var prevOrder = serviceProductService.findByUserAndType(user.getReference(), type);
        if (prevOrder.isEmpty()) {
            throw new EntityNotFoundException("Order not found");
        }
        return new ServiceOrderDto(prevOrder.get());
    }

    @PutMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ServiceProductDto create(@RequestBody ServiceProductCreateRequest request) {
        var product = new ServiceProduct();
        product.setDisplayName(request.displayName);
        product.setDescription(request.description);
        product.setPrice(request.price);
        product.setStackable(request.stackable);
        product.setCurrency(request.currency);
        product.setPictureUrl(request.pictureName);
        product.setType(request.type);
        product.setAvailable(true);
        serviceProductService.save(product);
        return dtoService.toServiceProductDto(product);
    }

    @PostMapping("/id/{id}/setlimitations")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void setLimitations(@PathVariable long id, @RequestBody SetLimitationsRequest request) {
        var optional = serviceProductService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("ServiceProduct not found");
        }
        var product = optional.get();
        product.setEndDate(request.endDate);
        product.setCount(request.count);
        product.setGroupName(request.groupName);
        serviceProductService.save(product);
    }

    @PostMapping("/id/{id}/setavailable")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void setAvailable(@PathVariable long id, @RequestBody SetAvailableRequest request) {
        var optional = serviceProductService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("ServiceProduct not found");
        }
        var product = optional.get();
        product.setAvailable(request.available);
        serviceProductService.save(product);
    }

    @PostMapping("/id/{id}/setprice")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void setPrice(@PathVariable long id, @RequestBody SetPriceRequest request) {
        var optional = serviceProductService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("ServiceProduct not found");
        }
        var product = optional.get();
        product.setPrice(request.price);
        serviceProductService.save(product);
    }

    @PostMapping("/id/{id}/update")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(@PathVariable long id, @RequestBody ServiceProductUpdateRequest request) {
        var optional = serviceProductService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("ServiceProduct not found");
        }
        var product = optional.get();
        product.setDisplayName(request.displayName);
        product.setDescription(request.description);
        serviceProductService.save(product);
    }

    public record BuyServiceRequest(long id, int quantity) {
    }

    public record ServiceProductCreateRequest(String displayName, String description, double price, boolean stackable,
                                              String currency, String pictureName, ServiceProduct.ServiceType type) {
    }

    public record SetAvailableRequest(boolean available) {
    }

    public record SetPriceRequest(double price) {
    }

    public record ServiceProductUpdateRequest(String displayName, String description) {
    }

    public record SetLimitationsRequest(LocalDateTime endDate, long count, String groupName) {
    }

    public record ServiceProductUpdatePictureRequest(String pictureName) {
    }
}
