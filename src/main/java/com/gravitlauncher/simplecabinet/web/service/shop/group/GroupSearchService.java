package com.gravitlauncher.simplecabinet.web.service.shop.group;

import com.gravitlauncher.simplecabinet.web.model.shop.GroupProduct;
import com.gravitlauncher.simplecabinet.web.repository.shop.GroupSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GroupSearchService {

    @Autowired
    private GroupSearchRepository repository;

    public Page<GroupProduct> findByDisplayName(String localName,Pageable pageable) {
        return repository.findByLocalName(localName, pageable);
    }
}
