package com.gravitlauncher.simplecabinet.web.repository.shop;

import com.gravitlauncher.simplecabinet.web.model.shop.ServiceProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceProductRepository extends JpaRepository<ServiceProduct, Long> {
    Page<ServiceProduct> findAllByAvailable(Pageable pageable, boolean available);

    Page<ServiceProduct> findByType(ServiceProduct.ServiceType type, Pageable pageable);
}
