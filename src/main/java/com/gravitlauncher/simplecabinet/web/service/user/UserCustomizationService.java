package com.gravitlauncher.simplecabinet.web.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gravitlauncher.simplecabinet.web.model.shop.ServiceOrder;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserGroup;
import com.gravitlauncher.simplecabinet.web.service.shop.group.delivery.GroupDeliveryService;
import com.gravitlauncher.simplecabinet.web.service.shop.service.ServiceProductService;

import java.time.LocalDateTime;

@Service
public class UserCustomizationService {
    @Autowired
    private GroupDeliveryService groupDeliveryService;
    @Autowired
    private ServiceProductService serviceProductService;
    @Autowired
    private UserService userService;

    @Transactional
    public void updatePrefixByOrder(User user, String prefix, ServiceOrder order) {
        final LocalDateTime endDate;
        var product = order.getProduct();
        if (product.getDays() <= 0) {
            if (product.getGroupName() != null) {
                UserGroup userGroup = null;
                for (var ug : user.getGroups()) {
                    if (ug.getGroup().getId().equals(product.getGroupName())) {
                        userGroup = ug;
                        break;
                    }
                }
                if (userGroup == null) {
                    throw new SecurityException("You don't have a required group");
                }
                endDate = userGroup.getEndDate();
            } else {
                endDate = null;
            }
        } else {
            endDate = LocalDateTime.now().plusDays(product.getDays() * order.getQuantity());
        }
        user.setPrefix(prefix);
        userService.save(user);
        groupDeliveryService.updatePrefix(prefix, user.getUuid(), endDate);
        serviceProductService.delivery(order);

    }

    @Transactional
    public void updatePrefixByGroup(User user, String prefix, String groupName) {
        UserGroup userGroup = null;
        for (var ug : user.getGroups()) {
            if (ug.getGroup().getId().equals(groupName)) {
                userGroup = ug;
                break;
            }
        }
        if (userGroup == null) {
            throw new SecurityException("You don't have a required group");
        }
        user.setPrefix(prefix);
        userService.save(user);
        groupDeliveryService.updatePrefix(prefix, user.getUuid(), userGroup.getEndDate());
    }

    @Transactional
    public void deletePrefix(User user) {
        user.setPrefix(null);
        userService.save(user);
        groupDeliveryService.deletePrefix(user.getUuid());
    }
}
