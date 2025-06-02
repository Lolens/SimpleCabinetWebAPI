package com.gravitlauncher.simplecabinet.web.repository.shop;

import com.gravitlauncher.simplecabinet.web.model.shop.GroupProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupProductRepository extends JpaRepository<GroupProduct, Long> {
    Page<GroupProduct> findByAvailable(Pageable pageable, boolean available);
}
