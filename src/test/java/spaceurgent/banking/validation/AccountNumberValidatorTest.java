package spaceurgent.banking.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spaceurgent.banking.TestConstants;
import spaceurgent.banking.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class AccountNumberValidatorTest {
    private AccountNumberValidator accountNumberValidator = new AccountNumberValidator();

    @Test
    void validate_withValidAccountNumber() {
        assertDoesNotThrow(() -> accountNumberValidator.validate(TestConstants.TEST_ACCOUNT_NUMBER));
    }

    @Test
    void validate_withNullAccountNumber() {
        assertThrows(NullPointerException.class, () -> accountNumberValidator.validate(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "123121231", "00000000000000", "2600 00000 00000"})
    void validate_withInvalidAccountNumber(String invalidAccountNumber) {
        final var exception = assertThrows(
                ValidationException.class,
                () -> accountNumberValidator.validate(invalidAccountNumber)
        );
        assertEquals("Invalid account number", exception.getMessage());
    }
}