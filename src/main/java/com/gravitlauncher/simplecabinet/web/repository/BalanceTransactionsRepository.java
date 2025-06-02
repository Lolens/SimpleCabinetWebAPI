package com.gravitlauncher.simplecabinet.web.repository;

import com.gravitlauncher.simplecabinet.web.model.BalanceTransaction;
import com.gravitlauncher.simplecabinet.web.model.user.UserBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BalanceTransactionsRepository extends JpaRepository<BalanceTransaction, Long> {
    @Query("select t from BalanceTransaction t where t.from = :balance or t.to = :balance order by createdAt desc")
    Page<BalanceTransaction> findAllByBalance(@Param("balance") UserBalance balance, Pageable pageable);
}
