package spaceurgent.banking.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spaceurgent.banking.exception.InvalidAmountException;

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
                InvalidAmountException.class,
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

    @ParameterizedTest
    @ValueSource(doubles = {-100, -10.231, 0, 0.009})
    void deposit_withInvalidAmount(Double doubleAmountValue) {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        final var depositAmount = BigDecimal.valueOf(doubleAmountValue).setScale(2, RoundingMode.FLOOR);
        final var exception = assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(depositAmount)
        );
        assertEquals("Transfer amount must be greater than 0", exception.getMessage());
    }
}