package com.gravitlauncher.simplecabinet.web.controller.shop;

import com.gravitlauncher.simplecabinet.web.dto.PageDto;
import com.gravitlauncher.simplecabinet.web.dto.shop.ItemOrderDto;
import com.gravitlauncher.simplecabinet.web.dto.shop.ItemProductDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.model.shop.ItemProduct;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.shop.item.ItemProductService;
import com.gravitlauncher.simplecabinet.web.service.shop.item.ItemSearchService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop/item/")
public class ItemShopController {
    @Autowired
    private ItemProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private DtoService dtoService;
    @Autowired
    private ItemSearchService search;

    @GetMapping("/page/{pageId}")
    public PageDto<ItemProductDto> getPage(@PathVariable int pageId) {
        var list = productService.findAllAvailable(PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(dtoService::toItemProductDto));
    }

    @GetMapping("/search/{data}/{pageId}")
    public PageDto<ItemProductDto> searchByData(@PathVariable String data, @PathVariable int pageId ) {
        var page = PageRequest.of(pageId, 10);
        var list = search.findByDisplayName(data, page);
        return new PageDto<>(list.map(dtoService::toItemProductDto));
    }

    @GetMapping("/id/{id}")
    public ItemProductDto getById(@PathVariable long id) {
        var optional = productService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("ItemProduct not found");
        }
        return dtoService.toItemProductDto(optional.get());
    }

    @PostMapping("/id/{id}/setlimitations")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void setLimitations(@PathVariable long id, @RequestBody GroupShopController.SetLimitationsRequest request) {
        var optional = productService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("GroupProduct not found");
        }
        var product = optional.get();
        product.setEndDate(request.endDate());
        product.setCount(request.count());
        product.setGroupName(request.groupName());
        productService.save(product);
    }

    @PostMapping("/id/{id}/setavailable")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void setAvailable(@PathVariable long id, @RequestBody GroupShopController.SetAvailableRequest request) {
        var optional = productService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("GroupProduct not found");
        }
        var product = optional.get();
        product.setAvailable(request.available());
        productService.save(product);
    }

    @PostMapping("/id/{id}/update")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(@PathVariable long id, @RequestBody ItemShopController.ItemProductUpdateRequest request) {
        var optional = productService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Item not found");
        }
        var product = optional.get();
        product.setDisplayName(request.displayName);
        product.setDescription(request.description);
        productService.save(product);
    }

    @PostMapping("/id/{id}/updatepicture")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void updatePicture(@PathVariable long id, @RequestBody ItemShopController.ItemProductUpdatePictureRequest request) {
        var optional = productService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Item not found");
        }
        var product = optional.get();
        product.setPictureUrl(request.pictureName);
        productService.save(product);
    }

    @PostMapping("/id/{id}/setprice")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void setPrice(@PathVariable long id, @RequestBody GroupShopController.SetPriceRequest request) {
        var optional = productService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("GroupProduct not found");
        }
        var product = optional.get();
        product.setPrice(request.price());
        productService.save(product);
    }

    @PutMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ItemProductDto create(@RequestBody CreateItemRequest request) {
        ItemProduct product = new ItemProduct();
        product.setDisplayName(request.displayName);
        product.setDescription(request.description);
        product.setPrice(request.price);
        product.setCurrency(request.currency);
        product.setItemName(request.itemName);
        product.setItemExtra(request.itemExtra);
        product.setItemNbt(request.itemNbt);
        product.setItemCustom(request.itemCustom);
        product.setItemQuantity(request.itemQuantity);
        product.setServer(request.server);
        product.setPictureUrl(request.pictureName);
        product.setAvailable(true);
        productService.save(product);
        return dtoService.toItemProductDto(product);
    }

    @PostMapping("/buy")
    @PreAuthorize("isAuthenticated()")
    public ItemOrderDto buyItem(@RequestBody BuyItemRequest request) {
        var user = userService.getCurrentUser();
        var productOptional = productService.findById(request.id);
        if (productOptional.isEmpty()) {
            throw new InvalidParametersException("ItemProduct not found", 1);
        }
        if (request.quantity <= 0) {
            throw new InvalidParametersException("quantity <= 0", 2);
        }
        var product = productOptional.get();
        var order = productService.createItemOrder(product, request.quantity, user.getReference());
        productService.delivery(order);
        return new ItemOrderDto(order);
    }

    public record BuyItemRequest(long id, long quantity) {

    }

    public record CreateItemRequest(String displayName, String description, double price,
                                    String currency, String itemName, String itemExtra, String itemNbt,
                                    String itemCustom, int itemQuantity, String server, String pictureName) {

    }
    public record ItemProductUpdateRequest(String displayName, String description) {

    }

    public record ItemProductUpdatePictureRequest(String pictureName) {

    }
}
