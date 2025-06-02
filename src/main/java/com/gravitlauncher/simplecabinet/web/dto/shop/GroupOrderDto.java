package com.gravitlauncher.simplecabinet.web.dto.shop;

import com.gravitlauncher.simplecabinet.web.model.shop.GroupOrder;

public class GroupOrderDto extends OrderDto {
    public final String server;

    public GroupOrderDto(GroupOrder entity) {
        super(entity);
        server = entity.getServer();
    }
}
