package spaceurgent.banking.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spaceurgent.banking.exception.AmountExceedsBalanceException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;
import static spaceurgent.banking.TestUtils.DEFAULT_CURRENCY;

class AccountTest {
    @ParameterizedTest
    @ValueSource(doubles = {0, 0.1111, 0.1199, 100.1,  1000, Double.MAX_VALUE})
    @DisplayName("Create account with 0 or greater balance - OK")
    void createAccount_withValidInitialBalance_ok(Double initialBalance) {
        final var expectedBalance = BigDecimal.valueOf(initialBalance).setScale(2, RoundingMode.FLOOR);
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.valueOf(initialBalance));
        assertEquals(TEST_ACCOUNT_NUMBER, account.getNumber());
        assertEquals(expectedBalance, account.getBalance(), "Account balance differs from initial");
        assertEquals(DEFAULT_CURRENCY, account.getCurrency(), "Incorrect default currency");
    }

    @Test
    @DisplayName("Create account with null account number throws")
    void createAccount_withNullAccountNumber_throws() {
        assertThrows(NullPointerException.class, () -> new Account(null, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Create account with null balance throws")
    void createAccount_withNullInitialBalance_throws() {
        assertThrows(NullPointerException.class, () -> new Account(TEST_ACCOUNT_NUMBER, null));
    }

    @Test
    @DisplayName("Create account with negative balance throws")
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
    @DisplayName("Deposit with amount greater than 0 - OK")
    void deposit_withValidAmount_ok(Double doubleAmountValue) {
        final var initialBalance = BigDecimal.valueOf(100);
        final var account = new Account(TEST_ACCOUNT_NUMBER, initialBalance);
        final var depositAmount = BigDecimal.valueOf(doubleAmountValue);
        final var expectedBalance = initialBalance.add(depositAmount).setScale(2, RoundingMode.FLOOR);
        account.deposit(depositAmount);
        assertEquals(expectedBalance, account.getBalance());
    }

    @Test
    @DisplayName("Deposit with null amount throws")
    void deposit_withNullAmount_throws() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        assertThrows(NullPointerException.class, () -> account.deposit(null));
    }

    @Test
    @DisplayName("Deposit with negative amount throws")
    void deposit_withNegativeAmount_throws() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        final var exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(BigDecimal.valueOf(-1))
        );
        assertEquals("Transfer amount must be greater than 0", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-100, -10.231, 0, 0.009})
    @DisplayName("Deposit with negative amount or 0 amount throws")
    void deposit_withInvalidAmount_throws(Double doubleAmountValue) {
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
    @DisplayName("Withdraw with amount greater than 0 - OK")
    void withdraw_withValidAmount_ok(Double doubleAmountValue) throws AmountExceedsBalanceException {
        final var initialBalance = BigDecimal.valueOf(100);
        final var account = new Account(TEST_ACCOUNT_NUMBER, initialBalance);
        final var withdrawAmount = BigDecimal.valueOf(doubleAmountValue).setScale(2, RoundingMode.FLOOR);
        final var expectedBalance = initialBalance.subtract(withdrawAmount);
        account.withdraw(withdrawAmount);
        assertEquals(expectedBalance, account.getBalance());
    }

    @Test
    @DisplayName("Withdraw with null amount throws")
    void withdraw_withNullAmount() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        assertThrows(NullPointerException.class, () -> account.withdraw(null));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-100, -10.231, 0, 0.009})
    @DisplayName("Withdraw with negative amount or 0 amount throws")
    void withdraw_withInvalidAmount_throws() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.valueOf(100));
        final var exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(BigDecimal.valueOf(-1))
        );
        assertEquals("Transfer amount must be greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Withdraw with amount greater than balance throws")
    void withdraw_withAmountExceedBalance_throws() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        final var exception = assertThrows(
                AmountExceedsBalanceException.class,
                () -> account.withdraw(BigDecimal.valueOf(0.01))
        );
        assertEquals("Withdraw amount exceeds balance", exception.getMessage());
    }
}