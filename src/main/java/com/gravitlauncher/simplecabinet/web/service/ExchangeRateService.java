package com.gravitlauncher.simplecabinet.web.service;

import com.gravitlauncher.simplecabinet.web.exception.BalanceException;
import com.gravitlauncher.simplecabinet.web.model.ExchangeRate;
import com.gravitlauncher.simplecabinet.web.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ExchangeRateService {
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    public Optional<ExchangeRate> findByCurrency(String fromCurrency, String toCurrency) {
        return exchangeRateRepository.findExchangeRateByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
    }

    public List<ExchangeRate> findExchangeRateByFromCurrency(String fromCurrency) {
        return exchangeRateRepository.findExchangeRateByFromCurrency(fromCurrency);
    }

    public <S extends ExchangeRate> S save(S entity) {
        return exchangeRateRepository.save(entity);
    }

    public Optional<ExchangeRate> findById(Long aLong) {
        return exchangeRateRepository.findById(aLong);
    }

    public Page<ExchangeRate> findAll(Pageable pageable) {
        return exchangeRateRepository.findAll(pageable);
    }

    public ExchangeRate create(String fromCurrency, String toCurrency, double value, boolean unsafe) {
        var rate = new ExchangeRate();
        rate.setFromCurrency(fromCurrency);
        rate.setToCurrency(toCurrency);
        rate.setValue(value);
        if (!unsafe) {
            checkRateSafety(rate);
        }
        exchangeRateRepository.save(rate);
        return rate;
    }

    @Transactional
    public void checkRateSafety(ExchangeRate rate) {
        Set<String> checkedCurrency = new HashSet<>();
        rateSafetyDfs(rate.getFromCurrency(), rate.getToCurrency(), checkedCurrency, 1.0 / rate.getValue(), 1.0 / rate.getValue());
    }

    private void rateSafetyDfs(String initialCurrency, String currency, Set<String> checked, double k, double initialK) {
        if (currency.equals(initialCurrency)) {
            if (Double.compare(k, initialK * 1.00001) > 0) {
                throw new BalanceException("ExchangeRate not safety");
            }
        }
        if (checked.contains(currency)) return;
        var list = findExchangeRateByFromCurrency(currency);
        checked.add(currency);
        for (var e : list) {
            rateSafetyDfs(initialCurrency, e.getToCurrency(), checked, k * e.getValue(), initialK);
        }
    }
}
