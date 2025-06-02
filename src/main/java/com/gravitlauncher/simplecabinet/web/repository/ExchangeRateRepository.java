package com.gravitlauncher.simplecabinet.web.repository;

import com.gravitlauncher.simplecabinet.web.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findExchangeRateByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);

    List<ExchangeRate> findExchangeRateByFromCurrency(String fromCurrency);
}
