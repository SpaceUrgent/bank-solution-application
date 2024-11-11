package spaceurgent.banking.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class AccountNumberGeneratorTest {

    private static final String ACCOUNT_NUMBER_REGEX = "2600\\d{10}";

    @Test
    @DisplayName("Next account number - OK")
    void nextAccountNumber_ok() {
        final var accountNumberGenerator = new AccountNumberGenerator();
        final var invocationsTotal = 100;
        final var accountNumberSet = new HashSet<String>();
        for (int i = 0; i < invocationsTotal; i++) {
            var accountNumber = accountNumberGenerator.nextAccountNumber();
            assertTrue(accountNumber.matches(ACCOUNT_NUMBER_REGEX), "Account number doesn't match account regexp");
            accountNumberSet.add(accountNumber);
        }
        assertEquals(invocationsTotal, accountNumberSet.size(), "Account number list size doesn't match invocation number");
    }
}