package com.gravitlauncher.simplecabinet.web.controller;

import com.gravitlauncher.simplecabinet.web.dto.ExchangeRateDto;
import com.gravitlauncher.simplecabinet.web.dto.PageDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.service.ExchangeRateService;
import com.gravitlauncher.simplecabinet.web.service.user.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exchangerate")
public class ExchangeRateController {
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private ExchangeRateService exchangeRateService;

    @PutMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ExchangeRateDto create(@RequestBody ExchangeRateCreateRequest request) {
        var result = exchangeRateService.create(request.fromCurrency, request.toCurrency, request.value, request.unsafe);
        return new ExchangeRateDto(result);
    }

    @GetMapping("/page/{pageId}")
    public PageDto<ExchangeRateDto> getPage(@PathVariable int pageId) {
        var list = exchangeRateService.findAll(PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(ExchangeRateDto::new));
    }

    @GetMapping("/id/{id}")
    public ExchangeRateDto getById(@PathVariable long id) {
        var optional = exchangeRateService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("ExchangeRate not found");
        }
        return new ExchangeRateDto(optional.get());
    }

    @GetMapping("/get/{fromCurrency}/{toCurrency}")
    public ExchangeRateDto getByCurrency(@PathVariable String fromCurrency, @PathVariable String toCurrency) {
        var optional = exchangeRateService.findByCurrency(fromCurrency, toCurrency);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("ExchangeRate not found");
        }
        return new ExchangeRateDto(optional.get());
    }

    @GetMapping("/get/{fromCurrency}")
    public List<ExchangeRateDto> getByCurrency(@PathVariable String fromCurrency) {
        var list = exchangeRateService.findExchangeRateByFromCurrency(fromCurrency);
        return list.stream().map(ExchangeRateDto::new).toList();
    }

    public record ExchangeRateCreateRequest(String fromCurrency, String toCurrency, double value, boolean unsafe) {
    }
}
