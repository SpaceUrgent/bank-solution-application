package spaceurgent.banking.utils;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class AccountNumberGeneratorTest {

    private static final String ACCOUNT_NUMBER_REGEX = "2600\\d{10}";

    @Test
    void nextAccountNumber() {
        final var invocationsTotal = 100;
        final var accountNumberSet = new HashSet<String>();
        for (int i = 0; i < invocationsTotal; i++) {
            var accountNumber = AccountNumberGenerator.nextAccountNumber();
            assertTrue(accountNumber.matches(ACCOUNT_NUMBER_REGEX));
            accountNumberSet.add(accountNumber);
        }
        assertEquals(invocationsTotal, accountNumberSet.size());
    }
}