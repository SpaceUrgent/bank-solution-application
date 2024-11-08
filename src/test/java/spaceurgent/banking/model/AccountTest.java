package spaceurgent.banking.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spaceurgent.banking.exception.InvalidAmountException;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @ParameterizedTest
    @ValueSource(longs = {0, 100, 1000, Long.MAX_VALUE})
    void createAccount_withValidInitialBalance(Long initialBalance) {
        final var account = new Account(initialBalance);
        assertEquals(initialBalance, account.getBalance(), "Account balance differs from initial");
    }

    @Test
    void createAccount_withNegativeInitialBalance_throws() {
        final var negativeInitialBalance = -1L;
        final var exception = assertThrows(
                InvalidAmountException.class,
                () -> new Account(negativeInitialBalance)
        );
        assertEquals("Initial balance can't be less than 0", exception.getMessage());
    }
}