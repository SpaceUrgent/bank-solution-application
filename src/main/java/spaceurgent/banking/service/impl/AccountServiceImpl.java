package spaceurgent.banking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spaceurgent.banking.model.Account;
import spaceurgent.banking.repository.AccountRepository;
import spaceurgent.banking.service.AccountService;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    public Account createAccount(BigDecimal initialBalance) {
        requireNonNull(initialBalance, "Initial balance is required");
        return accountRepository.save(new Account(initialBalance));
    }
}
