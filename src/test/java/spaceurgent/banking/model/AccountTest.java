package spaceurgent.banking.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spaceurgent.banking.exception.AmountExceedsBalanceException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;

class AccountTest {
    private final static Currency DEFAULT_CURRENCY = Currency.UAH;

    @ParameterizedTest
    @ValueSource(doubles = {0, 0.1111, 0.1199, 100.1,  1000, Double.MAX_VALUE})
    void createAccount_withValidInitialBalance(Double initialBalance) {
        final var expectedBalance = BigDecimal.valueOf(initialBalance).setScale(2, RoundingMode.FLOOR);
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.valueOf(initialBalance));
        assertEquals(TEST_ACCOUNT_NUMBER, account.getNumber());
        assertEquals(expectedBalance, account.getBalance(), "Account balance differs from initial");
        assertEquals(DEFAULT_CURRENCY, account.getCurrency(), "Incorrect default currency");
    }

    @Test
    void createAccount_withNullAccountNumber() {
        assertThrows(NullPointerException.class, () -> new Account(null, BigDecimal.ZERO));
    }

    @Test
    void createAccount_withNullInitialBalance() {
        assertThrows(NullPointerException.class, () -> new Account(TEST_ACCOUNT_NUMBER, null));
    }

    @Test
    void createAccount_withNegativeInitialBalance_throws() {
        final var negativeInitialBalance = BigDecimal.valueOf(-1L);
        final var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Account(TEST_ACCOUNT_NUMBER, negativeInitialBalance)
        );
        assertEquals("Initial balance can't be less than 0", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.1111, 0.9999, 1, 10.8, 1000, Double.MAX_VALUE})
    void deposit_withValidAmount(Double doubleAmountValue) {
        final var initialBalance = BigDecimal.valueOf(100);
        final var account = new Account(TEST_ACCOUNT_NUMBER, initialBalance);
        final var depositAmount = BigDecimal.valueOf(doubleAmountValue);
        final var expectedBalance = initialBalance.add(depositAmount).setScale(2, RoundingMode.FLOOR);
        account.deposit(depositAmount);
        assertEquals(expectedBalance, account.getBalance());
    }

    @Test
    void deposit_withNullAmount() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        assertThrows(NullPointerException.class, () -> account.deposit(null));
    }

    @Test
    void deposit_withNegativeAmount() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        final var exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(BigDecimal.valueOf(-1))
        );
        assertEquals("Transfer amount must be greater than 0", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-100, -10.231, 0, 0.009})
    void deposit_withInvalidAmount(Double doubleAmountValue) {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        final var depositAmount = BigDecimal.valueOf(doubleAmountValue).setScale(2, RoundingMode.FLOOR);
        final var exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(depositAmount)
        );
        assertEquals("Transfer amount must be greater than 0", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(doubles = {10, 10.111, 10.119, 20, 100})
    void withdraw_withValidAmount(Double doubleAmountValue) {
        final var initialBalance = BigDecimal.valueOf(100);
        final var account = new Account(TEST_ACCOUNT_NUMBER, initialBalance);
        final var withdrawAmount = BigDecimal.valueOf(doubleAmountValue).setScale(2, RoundingMode.FLOOR);
        final var expectedBalance = initialBalance.subtract(withdrawAmount);
        account.withdraw(withdrawAmount);
        assertEquals(expectedBalance, account.getBalance());
    }

    @Test
    void withdraw_withNullAmount() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        assertThrows(NullPointerException.class, () -> account.withdraw(null));
    }

    @Test
    void withdraw_withNegativeAmount() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.valueOf(100));
        final var exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(BigDecimal.valueOf(-1))
        );
        assertEquals("Transfer amount must be greater than 0", exception.getMessage());
    }

    @Test
    void withdraw_withAmountExceedBalance() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        final var exception = assertThrows(
                AmountExceedsBalanceException.class,
                () -> account.withdraw(BigDecimal.valueOf(0.01))
        );
        assertEquals("Withdraw amount exceeds balance", exception.getMessage());
    }
}