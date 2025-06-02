package com.gravitlauncher.simplecabinet.web.repository.shop;

import com.gravitlauncher.simplecabinet.web.model.shop.ItemProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemProductRepository extends JpaRepository<ItemProduct, Long> {

    Page<ItemProduct> findAllByAvailable(Pageable pageable, boolean available);
}
