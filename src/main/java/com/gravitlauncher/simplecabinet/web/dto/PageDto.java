package com.gravitlauncher.simplecabinet.web.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageDto<T> {
    public List<T> data;
    public int pageSize;
    public int totalPages;
    public long totalElements;

    public PageDto(Page<T> page) {
        data = page.stream().toList();
        pageSize = page.getSize();
        totalPages = page.getTotalPages();
        totalElements = page.getTotalElements();
    }
}
