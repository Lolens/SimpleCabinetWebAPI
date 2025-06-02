package com.gravitlauncher.simplecabinet.web.service.shop.group;

import com.gravitlauncher.simplecabinet.web.exception.BalanceException;
import com.gravitlauncher.simplecabinet.web.exception.InvalidParametersException;
import com.gravitlauncher.simplecabinet.web.model.shop.GroupOrder;
import com.gravitlauncher.simplecabinet.web.model.shop.GroupProduct;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserGroup;
import com.gravitlauncher.simplecabinet.web.repository.shop.GroupOrderRepository;
import com.gravitlauncher.simplecabinet.web.repository.shop.GroupProductRepository;
import com.gravitlauncher.simplecabinet.web.service.shop.ShopService;
import com.gravitlauncher.simplecabinet.web.service.shop.group.delivery.GroupDeliveryService;
import com.gravitlauncher.simplecabinet.web.service.user.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class GroupProductService {
    @Autowired
    private GroupProductRepository repository;
    @Autowired
    private GroupOrderRepository orderRepository;
    @Autowired
    private ShopService shopService;
    @Autowired
    private GroupDeliveryService deliveryService;
    @Autowired
    private UserGroupService userGroupService;

    public <S extends GroupProduct> S save(S entity) {
        return repository.save(entity);
    }

    public Page<GroupProduct> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<GroupProduct> findAllAvailable(Pageable pageable) {
        return repository.findByAvailable(pageable, true);
    }

    public Optional<GroupProduct> findById(Long aLong) {
        return repository.findById(aLong);
    }

    @Transactional
    public GroupOrder createGroupOrder(GroupProduct product, long quantity, User user) throws BalanceException {
        LocalDateTime now = LocalDateTime.now();
        if (product.getEndDate() != null && product.getEndDate().isBefore(now)) {
            throw new InvalidParametersException("Product expired", 3);
        }
        if (product.getCount() > 0) {
            if (product.getCount() < quantity) {
                throw new InvalidParametersException("Not enough product available", 4);
            }
            product.setCount(product.getCount() - quantity);
            if (product.getCount() == 0) {
                product.setAvailable(false);
            }
            save(product);
        }
        GroupOrder groupOrder = new GroupOrder();
        shopService.fillBasicOrderProperties(groupOrder, quantity, user);
        groupOrder.setProduct(product);
        orderRepository.save(groupOrder);
        shopService.makeTransaction(groupOrder, product);
        return groupOrder;
    }

    @Transactional
    public GroupOrder delivery(GroupOrder initialOrder) {
        var product = initialOrder.getProduct();
        var user = initialOrder.getUser();
        UserGroup group = userGroupService.findByGroupAndUser(product.getGroup(), user).orElse(null);
        if (group == null) {
            group = makeUserGroup(initialOrder);
        } else if (product.isStackable()) {
            group.setEndDate(group.getEndDate().plusDays(product.getExpireDays() * initialOrder.getQuantity()));
        } else {
            group.setEndDate(group.getStartDate().plusDays(product.getExpireDays() * initialOrder.getQuantity()));
        }
        userGroupService.save(group);
        shopService.fillProcessDeliveryOrderProperties(initialOrder);
        orderRepository.save(initialOrder);
        if (!product.isLocal()) {
            deliveryService.delivery(initialOrder);
        }
        return initialOrder;
    }

    private UserGroup makeUserGroup(GroupOrder order) {
        var product = order.getProduct();
        UserGroup userGroup = new UserGroup();
        userGroup.setGroup(product.getGroup());
        userGroup.setUser(order.getUser());
        userGroup.setStartDate(LocalDateTime.now());
        if(product.getExpireDays() > 0) {
            userGroup.setEndDate(userGroup.getStartDate().plusDays(product.getExpireDays()*order.getQuantity()));
        }
        return userGroup;
    }
}
