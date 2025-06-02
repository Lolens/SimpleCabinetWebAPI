package com.gravitlauncher.simplecabinet.web.repository.user;

import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<UserBalance, Long> {
    Page<UserBalance> findUserBalanceByUser(User user, Pageable pageable);

    Optional<UserBalance> findUserBalanceByUserAndCurrency(User user, String currency);
}
