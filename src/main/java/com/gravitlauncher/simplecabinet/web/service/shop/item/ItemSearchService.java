package com.gravitlauncher.simplecabinet.web.service.shop.item;

import com.gravitlauncher.simplecabinet.web.model.shop.ItemProduct;
import com.gravitlauncher.simplecabinet.web.repository.shop.ItemSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ItemSearchService {
    @Autowired
    private ItemSearchRepository repository;

    public Page<ItemProduct> findByDisplayName (String displayName, Pageable pageable) {
        return repository.findByDisplayName(displayName, pageable);
    }
}
