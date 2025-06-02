package com.gravitlauncher.simplecabinet.web.repository.shop;

import com.gravitlauncher.simplecabinet.web.model.shop.ItemDelivery;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDeliveryRepository extends JpaRepository<ItemDelivery, Long> {
    Page<ItemDelivery> findAllByUserAndCompleted(User user, boolean completed, Pageable pageable);
}
