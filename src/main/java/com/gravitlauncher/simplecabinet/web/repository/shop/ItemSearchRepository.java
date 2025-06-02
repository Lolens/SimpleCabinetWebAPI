package com.gravitlauncher.simplecabinet.web.repository.shop;

import com.gravitlauncher.simplecabinet.web.model.shop.ItemProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemSearchRepository extends JpaRepository<ItemProduct, Long> {

        @Query("SELECT ip FROM ItemProduct ip WHERE ip.displayName ilike %:displayName%")
        Page<ItemProduct> findByDisplayName(@Param("displayName") String displayName, Pageable pageable);
    }

