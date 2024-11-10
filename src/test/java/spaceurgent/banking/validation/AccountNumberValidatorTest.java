package spaceurgent.banking.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spaceurgent.banking.TestConstants;
import spaceurgent.banking.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class AccountNumberValidatorTest {
    private AccountNumberValidator accountNumberValidator = new AccountNumberValidator();

    @Test
    @DisplayName("Validate with valid account number - OK")
    void validate_withValidAccountNumber_ok() {
        assertDoesNotThrow(() -> accountNumberValidator.validate(TestConstants.TEST_ACCOUNT_NUMBER));
    }

    @Test
    @DisplayName("Validate with null account number throws")
    void validate_withNullAccountNumber_throws() {
        assertThrows(NullPointerException.class, () -> accountNumberValidator.validate(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "123121231", "00000000000000", "2600 00000 00000"})
    @DisplayName("Validate with invalid account number throws")
    void validate_withInvalidAccountNumber_throws(String invalidAccountNumber) {
        final var exception = assertThrows(
                ValidationException.class,
                () -> accountNumberValidator.validate(invalidAccountNumber)
        );
        assertEquals("Invalid account number", exception.getMessage());
    }
}