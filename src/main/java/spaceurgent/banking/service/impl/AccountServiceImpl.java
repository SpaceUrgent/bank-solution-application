package spaceurgent.banking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spaceurgent.banking.exception.AccountNotFoundException;
import spaceurgent.banking.model.Account;
import spaceurgent.banking.repository.AccountRepository;
import spaceurgent.banking.service.AccountService;
import spaceurgent.banking.utils.AccountNumberGenerator;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    @Override
    public Account createAccount(BigDecimal initialBalance) {
        requireNonNull(initialBalance, "Initial balance is required");
        final var accountNumber = accountNumberGenerator.nextAccountNumber();
        return accountRepository.save(new Account(accountNumber, initialBalance));
    }

    @Override
    public List<Account> findAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account findAccount(String accountNumber) {
        return findAccountOrThrow(accountNumber);
    }

    @Override
    public Account depositToAccount(String accountNumber, BigDecimal amount) {
        requireNonNull(accountNumber, "Amount is required");
        final var account = findAccountOrThrow(accountNumber);
        account.deposit(amount);
        return accountRepository.save(account);
    }

    @Override
    public Account withdrawFromAccount(String accountNumber, BigDecimal amount) {
        requireNonNull(accountNumber, "Amount is required");
        final var account = findAccountOrThrow(accountNumber);
        account.withdraw(amount);
        return accountRepository.save(account);
    }

    private Account findAccountOrThrow(String accountNumber) {
        requireNonNull(accountNumber, "Account number is required");
        return accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account with number '%s' not found".formatted(accountNumber)));
    }
}
