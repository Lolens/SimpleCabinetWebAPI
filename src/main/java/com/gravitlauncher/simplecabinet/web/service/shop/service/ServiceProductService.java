package com.gravitlauncher.simplecabinet.web.service.shop.service;

import com.gravitlauncher.simplecabinet.web.model.shop.ServiceOrder;
import com.gravitlauncher.simplecabinet.web.model.shop.ServiceProduct;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.repository.shop.ServiceOrderRepository;
import com.gravitlauncher.simplecabinet.web.repository.shop.ServiceProductRepository;
import com.gravitlauncher.simplecabinet.web.service.shop.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ServiceProductService {
    @Autowired
    private ServiceProductRepository repository;
    @Autowired
    private ServiceOrderRepository orderRepository;
    @Autowired
    private ShopService shopService;

    public Optional<ServiceProduct> findById(Long aLong) {
        return repository.findById(aLong);
    }

    public <S extends ServiceProduct> S save(S entity) {
        return repository.save(entity);
    }

    public Page<ServiceProduct> findAllAvailable(Pageable pageable) {
        return repository.findAllByAvailable(pageable, true);
    }

    public Optional<ServiceOrder> findByUserAndType(User user, ServiceProduct.ServiceType type) {
        return orderRepository.findByUserAndType(user, type);
    }

    public Page<ServiceProduct> findByType(ServiceProduct.ServiceType type, Pageable pageable) {
        return repository.findByType(type, pageable);
    }

    @Transactional
    public ServiceOrder createServiceOrder(ServiceProduct product, long quantity, User user) {
        var order = new ServiceOrder();
        shopService.fillBasicOrderProperties(order, quantity, user);
        order.setProduct(product);
        shopService.makeTransaction(order, product);
        orderRepository.save(order);
        return order;
    }

    public void delivery(ServiceOrder order) {
        shopService.fillProcessDeliveryOrderProperties(order);
        orderRepository.save(order);
    }
}
