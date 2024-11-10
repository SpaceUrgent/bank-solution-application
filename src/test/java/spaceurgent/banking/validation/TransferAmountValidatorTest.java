package spaceurgent.banking.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spaceurgent.banking.exception.ValidationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransferAmountValidatorTest {

    private final TransferAmountValidator transferAmountValidator = new TransferAmountValidator();

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 0.1, 10, 100})
    void validate_withValidAmount(Double amountDoubleValue) {
        assertDoesNotThrow(() -> transferAmountValidator.validate(BigDecimal.valueOf(amountDoubleValue)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, -0.01, 0, 0.001})
    void validate_withInvalidAmount(Double amountDoubleValue) {
        final var exception = assertThrows(
                ValidationException.class,
                () -> transferAmountValidator.validate(BigDecimal.valueOf(amountDoubleValue))
        );
        assertEquals("Invalid transfer amount. Amount must be grater than 0", exception.getMessage());
    }
}