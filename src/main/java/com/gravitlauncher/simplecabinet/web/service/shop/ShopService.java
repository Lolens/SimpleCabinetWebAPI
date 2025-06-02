package com.gravitlauncher.simplecabinet.web.service.shop;

import com.gravitlauncher.simplecabinet.web.exception.BalanceException;
import com.gravitlauncher.simplecabinet.web.model.BalanceTransaction;
import com.gravitlauncher.simplecabinet.web.model.shop.Order;
import com.gravitlauncher.simplecabinet.web.model.shop.Product;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.service.user.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShopService {
    @Autowired
    private BalanceService balanceService;

    public void fillBasicOrderProperties(Order<?> order, long quantity, User user) {
        order.setUser(user);
        order.setQuantity(quantity);
        order.setStatus(Order.OrderStatus.INITIATED);
        order.setCreatedAt(LocalDateTime.now());
    }

    public void fillProcessDeliveryOrderProperties(Order<?> order) {
        order.setStatus(Order.OrderStatus.DELIVERY);
        order.setUpdatedAt(LocalDateTime.now());
    }

    public BalanceTransaction makeTransaction(Order<?> order, Product product) throws BalanceException {
        var sum = order.getQuantity() * product.getPrice();
        var balance = balanceService.findOrCreateUserBalanceByUserAndCurrency(order.getUser(), product.getCurrency());
        return balanceService.removeMoney(balance, sum, "Shop");
    }
}
