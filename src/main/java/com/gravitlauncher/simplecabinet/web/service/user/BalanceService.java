package com.gravitlauncher.simplecabinet.web.service.user;

import com.gravitlauncher.simplecabinet.web.exception.BalanceException;
import com.gravitlauncher.simplecabinet.web.model.BalanceTransaction;
import com.gravitlauncher.simplecabinet.web.model.ExchangeRate;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserBalance;
import com.gravitlauncher.simplecabinet.web.repository.BalanceTransactionsRepository;
import com.gravitlauncher.simplecabinet.web.repository.user.BalanceRepository;
import com.gravitlauncher.simplecabinet.web.repository.user.UserRepository;
import com.gravitlauncher.simplecabinet.web.service.ExchangeRateService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BalanceService {
    @Autowired
    private BalanceRepository repository;
    @Autowired
    private BalanceTransactionsRepository transactionsRepository;
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public Optional<ExchangeRate> findExchangeRate(String fromCurrency, String toCurrency) {
        return exchangeRateService.findByCurrency(fromCurrency, toCurrency);
    }

    public Page<UserBalance> findUserBalanceByUser(User user, Pageable pageable) {
        return repository.findUserBalanceByUser(user, pageable);
    }

    public Page<BalanceTransaction> findAllByBalance(UserBalance balance, Pageable pageable) {
        return transactionsRepository.findAllByBalance(balance, pageable);
    }

    public Optional<UserBalance> findById(Long aLong) {
        return repository.findById(aLong);
    }

    public UserBalance getReference(Long aLong) {
        return repository.getReferenceById(aLong);
    }

    public UserBalance create(User user, String currency) {
        UserBalance balance = new UserBalance();
        balance.setUser(user);
        balance.setBalance(0.0);
        balance.setCurrency(currency);
        repository.save(balance);
        return balance;
    }

    public Optional<UserBalance> findUserBalanceByUserAndCurrency(User user, String currency) {
        return repository.findUserBalanceByUserAndCurrency(user, currency);
    }

    public UserBalance findOrCreateUserBalanceByUserAndCurrency(User user, String currency) {
        return findUserBalanceByUserAndCurrency(user, currency).orElseGet(() -> create(user, currency));
    }

    @Transactional
    public BalanceTransaction transfer(Long userId, Long fromId, Long toId, double fromCount, double toCount, boolean multiCurrency, String comment) {
        if (fromId != null && (Double.isNaN(fromCount) || Double.isInfinite(fromCount) || fromCount < 0.0)) {
            throw new BalanceException("Illegal fromCount (NaN, Inf, Neg)");
        }
        if (toId != null && (Double.isNaN(toCount) || Double.isInfinite(toCount) || toCount < 0.0)) {
            throw new BalanceException("Illegal toCount (NaN, Inf, Neg)");
        }
        if (fromId == null && toId == null) {
            throw new BalanceException("Illegal arguments: fromId and toId is null");
        }
        if (fromId != null) {
            Query fromUpdate = entityManager.createQuery("update UserBalance f set f.balance = f.balance - :fromCount where f.id = :id and f.balance >= :fromCount");
            fromUpdate.setParameter("fromCount", fromCount);
            fromUpdate.setParameter("id", fromId);
            int updated = fromUpdate.executeUpdate();
            if (updated != 1) {
                throw new BalanceException("Insufficient funds");
            }
        }
        if (toId != null) {
            Query toUpdate = entityManager.createQuery("update UserBalance f set f.balance = f.balance + :toCount where f.id = :id");
            toUpdate.setParameter("toCount", toCount);
            toUpdate.setParameter("id", toId);
            int updated = toUpdate.executeUpdate();
            if (updated != 1) {
                throw new BalanceException("Illegal state: target id not found");
            }
        }
        BalanceTransaction transaction = new BalanceTransaction();
        transaction.setUser(userId == null ? null : userRepository.getReferenceById(userId));
        transaction.setFrom(fromId == null ? null : repository.getReferenceById(fromId));
        transaction.setTo(toId == null ? null : repository.getReferenceById(toId));
        transaction.setFromCount(fromId == null ? 0.0 : -fromCount);
        transaction.setToCount(toId == null ? 0.0 : toCount);
        transaction.setMulticurrency(multiCurrency);
        transaction.setComment(comment);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionsRepository.save(transaction);
        return transaction;
    }

    @Transactional
    public BalanceTransaction transfer(Long userId, long fromId, long toId, ExchangeRate rate, double count, String comment) {
        return transfer(userId, fromId, toId, count, count * rate.getValue(), true, comment);
    }

    @Transactional
    public BalanceTransaction transfer(Long userId, long fromId, long toId, String fromCurrency, String toCurrency, double count, String comment, ExchangeRate exchangeRate) {
        if (fromCurrency == null || toCurrency == null) {
            throw new BalanceException("fromCurrency or toCurrency is null");
        }
        if (exchangeRate != null) {
            return transfer(userId, fromId, toId, count, count * exchangeRate.getValue(), !fromCurrency.equals(toCurrency), comment);
        } else {
            return transfer(userId, fromId, toId, count, count, !fromCurrency.equals(toCurrency), comment);
        }
    }

    @Transactional
    public BalanceTransaction addMoney(long toId, double count, String comment) throws BalanceException {
        return transfer(null, null, toId, 0.0, count, false, comment);
    }

    @Transactional
    public BalanceTransaction removeMoney(long fromId, double count, String comment) throws BalanceException {
        return transfer(null, fromId, null, count, 0.0, false, comment);
    }

    @Transactional
    public BalanceTransaction addMoney(UserBalance to, double count, String comment) throws BalanceException {
        return addMoney(to.getId(), count, comment);
    }

    @Transactional
    public BalanceTransaction removeMoney(UserBalance from, double count, String comment) throws BalanceException {
        return removeMoney(from.getId(), count, comment);
    }
}
