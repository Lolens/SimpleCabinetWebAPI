package com.gravitlauncher.simplecabinet.web.controller.cabinet;

import com.gravitlauncher.simplecabinet.web.dto.BalanceTransactionDto;
import com.gravitlauncher.simplecabinet.web.dto.PageDto;
import com.gravitlauncher.simplecabinet.web.dto.user.UserBalanceDto;
import com.gravitlauncher.simplecabinet.web.exception.AuthException;
import com.gravitlauncher.simplecabinet.web.exception.BalanceException;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.model.ExchangeRate;
import com.gravitlauncher.simplecabinet.web.model.user.UserBalance;
import com.gravitlauncher.simplecabinet.web.service.user.BalanceService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/cabinet/money")
public class CabinetMoneyController {
    @Autowired
    private UserService userService;
    @Autowired
    private BalanceService balanceService;

    @GetMapping("/balance/page/{pageId}")
    public PageDto<UserBalanceDto> getBalancePage(@PathVariable int pageId) {
        var user = userService.getCurrentUser();
        var list = balanceService.findUserBalanceByUser(user.getReference(), PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(UserBalanceDto::new));
    }

    @GetMapping("/balance/id/{id}")
    public UserBalanceDto getBalanceById(@PathVariable long id) {
        var user = userService.getCurrentUser();
        var optional = balanceService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Balance not found");
        }
        if (optional.get().getUser().getId() != user.getId()) {
            throw new AuthException("Access denied");
        }
        return new UserBalanceDto(optional.get());
    }

    @GetMapping("/balance/id/{id}/transactions/page/{pageId}")
    public PageDto<BalanceTransactionDto> getBalanceById(@PathVariable long id, @PathVariable int pageId) {
        var user = userService.getCurrentUser();
        var optional = balanceService.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Balance not found");
        }
        if (optional.get().getUser().getId() != user.getId()) {
            throw new AuthException("Access denied");
        }
        var list = balanceService.findAllByBalance(optional.get(), PageRequest.of(pageId, 10));
        return new PageDto<>(list.map(BalanceTransactionDto::new));
    }

    @GetMapping("/balance/currency/{currency}")
    public UserBalanceDto getBalanceByCurrency(@PathVariable String currency) {
        var user = userService.getCurrentUser();
        var optional = balanceService.findUserBalanceByUserAndCurrency(user.getReference(), currency);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Balance not found");
        }
        return new UserBalanceDto(optional.get());
    }

    @PostMapping("/transfer/{fromCurrency}/to/{userId}/{toCurrency}")
    @Transactional
    public BalanceTransactionDto transfer(@PathVariable String fromCurrency, @PathVariable long userId, @PathVariable String toCurrency, @RequestBody TransferRequest request) throws BalanceException {
        var user = userService.getCurrentUser();
        var ref = user.getReference();
        var fromBalanceOptional = balanceService.findUserBalanceByUserAndCurrency(ref, fromCurrency);
        if (fromBalanceOptional.isEmpty()) {
            throw new EntityNotFoundException("Your balance not found");
        }
        var targetUser = userService.findById(userId);
        if (targetUser.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        Optional<ExchangeRate> rate = balanceService.findExchangeRate(fromCurrency, toCurrency);
        if (rate.isEmpty()) {
            if (fromCurrency.equals(toCurrency)) {
                throw new BalanceException(String.format("You can't transfer %s", fromCurrency));
            }
            throw new BalanceException(String.format("Can't convert money from %s to %s", fromCurrency, toCurrency));
        }
        Optional<UserBalance> toBalanceOptional;
        toBalanceOptional = Optional.of(balanceService.findOrCreateUserBalanceByUserAndCurrency(targetUser.get(), toCurrency));
        var fromBalance = fromBalanceOptional.get();
        var toBalance = toBalanceOptional.get();
        var transaction = balanceService.transfer(user.getId(), fromBalance.getId(), toBalance.getId(),
                fromBalance.getCurrency(), toBalance.getCurrency(),
                request.count, request.comment, rate.get());
        return new BalanceTransactionDto(transaction);
    }

    public record TransferRequest(long count, String comment) {
    }
}
