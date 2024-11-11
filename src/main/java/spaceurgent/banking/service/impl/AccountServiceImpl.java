package spaceurgent.banking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spaceurgent.banking.dto.TransferRequestDto;
import spaceurgent.banking.exception.AccountNotFoundException;
import spaceurgent.banking.exception.AmountExceedsBalanceException;
import spaceurgent.banking.model.Account;
import spaceurgent.banking.repository.AccountRepository;
import spaceurgent.banking.service.AccountService;
import spaceurgent.banking.service.ValidationService;
import spaceurgent.banking.utils.AccountNumberGenerator;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final ValidationService validationService;
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    @Override
    public Account createAccount(BigDecimal initialBalance) {
        validationService.validateBalanceAmount(initialBalance);
        final var accountNumber = accountNumberGenerator.nextAccountNumber();
        return accountRepository.save(new Account(accountNumber, initialBalance));
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account getAccount(String accountNumber) {
        validationService.validateAccountNumber(accountNumber);
        return findAccountOrThrow(accountNumber);
    }

    @Override
    public Account depositToAccount(String accountNumber, BigDecimal amount) {
        validationService.validateAccountNumber(accountNumber);
        validationService.validateTransferAmount(amount);
        final var account = findAccountOrThrow(accountNumber);
        account.deposit(amount);
        return accountRepository.save(account);
    }

    @Override
    public Account withdrawFromAccount(String accountNumber, BigDecimal amount) throws AmountExceedsBalanceException {
        validationService.validateAccountNumber(accountNumber);
        validationService.validateTransferAmount(amount);
        final var account = findAccountOrThrow(accountNumber);
        account.withdraw(amount);
        return accountRepository.save(account);
    }

    @Transactional
    @Override
    public Account transferToAccount(TransferRequestDto transferRequest) throws AmountExceedsBalanceException {
        validationService.validateTransferRequestDto(transferRequest);
        final var sourceAccount = findAccountOrThrow(transferRequest.getSourceAccountNumber());
        final var targetAccount = findAccountOrThrow(transferRequest.getTargetAccountNumber());
        sourceAccount.withdraw(transferRequest.getAmount());
        targetAccount.deposit(transferRequest.getAmount());
        accountRepository.saveAll(List.of(sourceAccount, targetAccount));
        return sourceAccount;
    }

    private Account findAccountOrThrow(String accountNumber) {
        return accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account with number '%s' not found".formatted(accountNumber)));
    }
}
