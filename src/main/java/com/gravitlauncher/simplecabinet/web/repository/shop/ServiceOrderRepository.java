package com.gravitlauncher.simplecabinet.web.repository.shop;

import com.gravitlauncher.simplecabinet.web.model.shop.ServiceOrder;
import com.gravitlauncher.simplecabinet.web.model.shop.ServiceProduct;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    @Query("select o from ServiceOrder o join fetch o.product where o.user = ?1 and o.product.type = ?2 and o.status = 0")
    Optional<ServiceOrder> findByUserAndType(User user, ServiceProduct.ServiceType type);
}
