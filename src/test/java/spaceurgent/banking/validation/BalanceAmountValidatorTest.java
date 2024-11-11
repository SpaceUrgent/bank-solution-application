package spaceurgent.banking.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spaceurgent.banking.exception.ValidationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BalanceAmountValidatorTest {
    private final BalanceAmountValidator balanceAmountValidator = new BalanceAmountValidator();

    @ParameterizedTest
    @ValueSource(doubles = {0, 0.0001, 0.1, 10, 100})
    @DisplayName("Validate with valid balance - OK")
    void validate_withValidBalance_ok(Double balanceDoubleValue) {
        assertDoesNotThrow(() -> balanceAmountValidator.validate(BigDecimal.valueOf(balanceDoubleValue)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.001, -0.1, -1, -100})
    @DisplayName("Validate with negative balance throws")
    void validate_withNegativeBalance_throws(Double balanceDoubleValue) {
        final var exception = assertThrows(
                ValidationException.class,
                () -> balanceAmountValidator.validate(BigDecimal.valueOf(balanceDoubleValue))
        );
        assertEquals("Invalid balance. Balance must be equal or greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Validate with null balance throws")
    void validate_withNullBalance_throws() {
        assertThrows(NullPointerException.class, () -> balanceAmountValidator.validate(null));
    }
}