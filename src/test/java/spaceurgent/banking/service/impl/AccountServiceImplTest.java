package spaceurgent.banking.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spaceurgent.banking.exception.AccountNotFoundException;
import spaceurgent.banking.model.Account;
import spaceurgent.banking.repository.AccountRepository;
import spaceurgent.banking.utils.AccountNumberGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountNumberGenerator accountNumberGenerator;
    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void createAccount() {
        final var initialBalance = BigDecimal.ZERO.setScale(2, RoundingMode.FLOOR);
        doReturn(TEST_ACCOUNT_NUMBER).when(accountNumberGenerator).nextAccountNumber();
        doAnswer(invocation -> invocation.getArguments()[0]).when(accountRepository).save(any());
        final var created = accountService.createAccount(initialBalance);
        assertEquals(TEST_ACCOUNT_NUMBER, created.getNumber(), "Created account doesn't match expected");
        assertEquals(initialBalance, created.getBalance(), "Initial balanced doesn't match expected");
    }

    @Test
    void findAccounts() {
        final var accounts = randomAccounts();
        doReturn(accounts).when(accountRepository).findAll();
        assertEquals(accounts, accountService.findAccounts());
    }

    @Test
    void findAccount_withExistingNumber() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        doReturn(Optional.of(account)).when(accountRepository).findByNumber(eq(TEST_ACCOUNT_NUMBER));
        assertEquals(account, accountService.findAccount(TEST_ACCOUNT_NUMBER));
    }

    @Test
    void findAccount_withNonExistingNumber() {
        doReturn(Optional.empty()).when(accountRepository).findByNumber(any());
        final var exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.findAccount(TEST_ACCOUNT_NUMBER)
        );
        assertEquals("Account with number '%s' not found".formatted(TEST_ACCOUNT_NUMBER), exception.getMessage());
    }

    @Test
    void depositToAccount_ok() {
        final var initialBalance = BigDecimal.valueOf(10);
        final var account = new Account(TEST_ACCOUNT_NUMBER, initialBalance);
        final var depositAmount = BigDecimal.valueOf(100);
        final var expectedBalance = initialBalance.add(depositAmount);
        doReturn(Optional.of(account)).when(accountRepository).findByNumber(eq(TEST_ACCOUNT_NUMBER));
        doAnswer(invocation -> invocation.getArguments()[0]).when(accountRepository).save(any());
        final var updatedAccount = accountService.depositToAccount(TEST_ACCOUNT_NUMBER, depositAmount);
        assertEquals(expectedBalance, updatedAccount.getBalance());
    }

    @Test
    void depositToAccount_withNonExistingNumber() {
        final var depositAmount = BigDecimal.valueOf(100);
        doReturn(Optional.empty()).when(accountRepository).findByNumber(any());
        final var exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.depositToAccount(TEST_ACCOUNT_NUMBER, depositAmount)
        );
        assertEquals("Account with number '%s' not found".formatted(TEST_ACCOUNT_NUMBER), exception.getMessage());
    }

    @Test
    void withdrawFromAccount_ok() {
        final var initialBalance = BigDecimal.valueOf(100);
        final var account = new Account(TEST_ACCOUNT_NUMBER, initialBalance);
        final var withdrawAmount = BigDecimal.valueOf(10);
        final var expectedBalance = initialBalance.subtract(withdrawAmount);
        doReturn(Optional.of(account)).when(accountRepository).findByNumber(eq(TEST_ACCOUNT_NUMBER));
        doAnswer(invocation -> invocation.getArguments()[0]).when(accountRepository).save(any());
        final var updatedAccount = accountService.withdrawFromAccount(TEST_ACCOUNT_NUMBER, withdrawAmount);
        assertEquals(expectedBalance, updatedAccount.getBalance());
    }

    @Test
    void withdrawFromAccount_withNonExistingNumber() {
        final var withdrawAmount = BigDecimal.valueOf(10);
        doReturn(Optional.empty()).when(accountRepository).findByNumber(any());
        final var exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.withdrawFromAccount(TEST_ACCOUNT_NUMBER, withdrawAmount)
        );
        assertEquals("Account with number '%s' not found".formatted(TEST_ACCOUNT_NUMBER), exception.getMessage());
    }

    private List<Account> randomAccounts() {
        return IntStream.range(0, 10)
                .mapToObj(index -> new Account(String.valueOf(index), BigDecimal.valueOf(index)))
                .toList();
    }
}