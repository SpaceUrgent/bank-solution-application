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
}