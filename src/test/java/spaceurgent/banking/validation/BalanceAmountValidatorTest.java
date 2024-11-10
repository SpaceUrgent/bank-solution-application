package spaceurgent.banking.validation;

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
    void validate_withValidBalance(Double balanceDoubleValue) {
        assertDoesNotThrow(() -> balanceAmountValidator.validate(BigDecimal.valueOf(balanceDoubleValue)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.001, -0.1, -1, -100})
    void validate_withNegativeBalance(Double balanceDoubleValue) {
        final var exception = assertThrows(
                ValidationException.class,
                () -> balanceAmountValidator.validate(BigDecimal.valueOf(balanceDoubleValue))
        );
        assertEquals("Invalid balance. Balance must be equal or greater than 0", exception.getMessage());
    }

    @Test
    void validate_withNullBalance() {
        assertThrows(NullPointerException.class, () -> balanceAmountValidator.validate(null));
    }
}