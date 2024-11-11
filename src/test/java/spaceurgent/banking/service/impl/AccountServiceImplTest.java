package spaceurgent.banking.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spaceurgent.banking.Constants;
import spaceurgent.banking.dto.TransferRequestDto;
import spaceurgent.banking.exception.AccountNotFoundException;
import spaceurgent.banking.exception.AmountExceedsBalanceException;
import spaceurgent.banking.model.Account;
import spaceurgent.banking.repository.AccountRepository;
import spaceurgent.banking.service.ValidationService;
import spaceurgent.banking.utils.AccountNumberGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static spaceurgent.banking.TestConstants.DEFAULT_SOURCE_ACCOUNT_NUMBER;
import static spaceurgent.banking.TestConstants.DEFAULT_TARGET_ACCOUNT_NUMBER;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;
import static spaceurgent.banking.TestUtils.randomAccounts;
import static spaceurgent.banking.TestUtils.returnInputAnswer;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountNumberGenerator accountNumberGenerator;
    @Mock
    private ValidationService validationService;
    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    @DisplayName("Create account - OK")
    void createAccount_ok() {
        final var initialBalance = BigDecimal.ZERO.setScale(Constants.DEFAULT_AMOUNT_SCALE, RoundingMode.FLOOR);
        doReturn(TEST_ACCOUNT_NUMBER).when(accountNumberGenerator).nextAccountNumber();
        doAnswer(returnInputAnswer()).when(accountRepository).save(any());
        final var created = accountService.createAccount(initialBalance);
        assertEquals(TEST_ACCOUNT_NUMBER, created.getNumber(), "Created account doesn't match expected");
        assertEquals(initialBalance, created.getBalance(), "Initial balanced doesn't match expected");
    }

    @Test
    @DisplayName("Get accounts - OK")
    void getAccounts_ok() {
        final var accounts = randomAccounts();
        doReturn(accounts).when(accountRepository).findAll();
        assertEquals(accounts, accountService.getAccounts());
    }

    @Test
    @DisplayName("Get account with existing account number - OK")
    void getAccount_withExistingNumber_ok() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        doReturn(Optional.of(account)).when(accountRepository).findByNumber(eq(TEST_ACCOUNT_NUMBER));
        assertEquals(account, accountService.getAccount(TEST_ACCOUNT_NUMBER));
    }

    @Test
    @DisplayName("Get account with non-existing account number throws")
    void findAccount_withNonExistingNumber_throws() {
        doReturn(Optional.empty()).when(accountRepository).findByNumber(any());
        final var exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccount(TEST_ACCOUNT_NUMBER)
        );
        assertEquals("Account with number '%s' not found".formatted(TEST_ACCOUNT_NUMBER), exception.getMessage());
    }

    @Test
    @DisplayName("Deposit to account - OK")
    void depositToAccount_ok() {
        final var initialBalance = BigDecimal.valueOf(10);
        final var account = new Account(TEST_ACCOUNT_NUMBER, initialBalance);
        final var depositAmount = BigDecimal.valueOf(100);
        final var expectedBalance = initialBalance.add(depositAmount).setScale(Constants.DEFAULT_AMOUNT_SCALE, RoundingMode.FLOOR);
        doReturn(Optional.of(account)).when(accountRepository).findByNumber(eq(TEST_ACCOUNT_NUMBER));
        doAnswer(returnInputAnswer()).when(accountRepository).save(any());
        final var updatedAccount = accountService.depositToAccount(TEST_ACCOUNT_NUMBER, depositAmount);
        assertEquals(expectedBalance, updatedAccount.getBalance());
    }

    @Test
    @DisplayName("Deposit to account with non-existing account number throws")
    void depositToAccount_withNonExistingNumber_throws() {
        final var depositAmount = BigDecimal.valueOf(100);
        doReturn(Optional.empty()).when(accountRepository).findByNumber(any());
        final var exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.depositToAccount(TEST_ACCOUNT_NUMBER, depositAmount)
        );
        assertEquals("Account with number '%s' not found".formatted(TEST_ACCOUNT_NUMBER), exception.getMessage());
    }

    @Test
    @DisplayName("Withdraw from account - OK")
    void withdrawFromAccount_ok() throws AmountExceedsBalanceException {
        final var initialBalance = BigDecimal.valueOf(100);
        final var account = new Account(TEST_ACCOUNT_NUMBER, initialBalance);
        final var withdrawAmount = BigDecimal.valueOf(10);
        final var expectedBalance = initialBalance.subtract(withdrawAmount).setScale(2, RoundingMode.FLOOR);
        doReturn(Optional.of(account)).when(accountRepository).findByNumber(eq(TEST_ACCOUNT_NUMBER));
        doAnswer(invocation -> invocation.getArguments()[0]).when(accountRepository).save(any());
        final var updatedAccount = accountService.withdrawFromAccount(TEST_ACCOUNT_NUMBER, withdrawAmount);
        assertEquals(expectedBalance, updatedAccount.getBalance());
    }

    @Test
    @DisplayName("Withdraw from account with non-existing account number throws")
    void withdrawFromAccount_withNonExistingNumber_throws() {
        final var withdrawAmount = BigDecimal.valueOf(10);
        doReturn(Optional.empty()).when(accountRepository).findByNumber(any());
        final var exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.withdrawFromAccount(TEST_ACCOUNT_NUMBER, withdrawAmount)
        );
        assertEquals("Account with number '%s' not found".formatted(TEST_ACCOUNT_NUMBER), exception.getMessage());
    }

    @Test
    @DisplayName("Transfer to account - OK")
    void transferToAccount_ok() throws AmountExceedsBalanceException {
        final var sourceAccount = new Account(DEFAULT_SOURCE_ACCOUNT_NUMBER, BigDecimal.valueOf(100));
        final var targetAccount = new Account(DEFAULT_TARGET_ACCOUNT_NUMBER, BigDecimal.valueOf(0));
        final var amount = BigDecimal.valueOf(10);
        final var expectedSourceAccount = new Account(
                sourceAccount.getNumber(),
                sourceAccount.getBalance().subtract(amount).setScale(2, RoundingMode.FLOOR)
        );
        final var expectedTargetAccount = new Account(
                targetAccount.getNumber(),
                targetAccount.getBalance().add(amount).setScale(2, RoundingMode.FLOOR)
        );
        doReturn(Optional.of(sourceAccount)).when(accountRepository).findByNumber(eq(sourceAccount.getNumber()));
        doReturn(Optional.of(targetAccount)).when(accountRepository).findByNumber(eq(targetAccount.getNumber()));
        final var request = new TransferRequestDto(
                sourceAccount.getNumber(),
                targetAccount.getNumber(),
                amount
        );
        final var account = accountService.transferToAccount(request);
        assertEquals(expectedSourceAccount, account);
        verify(accountRepository).saveAll(eq(List.of(expectedSourceAccount, expectedTargetAccount)));
    }

    @Test
    @DisplayName("Transfer to account with non-existing source account number throws")
    void transferToAccount_withNonExistingSourceAccount_throws() {
        doReturn(Optional.empty()).when(accountRepository).findByNumber(eq(TEST_ACCOUNT_NUMBER));
        final var request = new TransferRequestDto(
                TEST_ACCOUNT_NUMBER, TEST_ACCOUNT_NUMBER, BigDecimal.valueOf(10)
        );
        final var exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.transferToAccount(request)
        );
        assertEquals("Account with number '%s' not found".formatted(TEST_ACCOUNT_NUMBER), exception.getMessage());
    }

    @Test
    @DisplayName("Transfer to account with non-existing target account number throws")
    void transferToAccount_withNonExistingTargetAccount_throws() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.valueOf(100));
        final var nonExistingAccountNumber = "26000000000002";
        doReturn(Optional.of(account)).when(accountRepository).findByNumber(eq(TEST_ACCOUNT_NUMBER));
        doReturn(Optional.empty()).when(accountRepository).findByNumber(eq(nonExistingAccountNumber));
        final var request = new TransferRequestDto(
                TEST_ACCOUNT_NUMBER, nonExistingAccountNumber, BigDecimal.valueOf(10)
        );
        final var exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.transferToAccount(request)
        );
        assertEquals("Account with number '%s' not found".formatted(nonExistingAccountNumber), exception.getMessage());
    }
}