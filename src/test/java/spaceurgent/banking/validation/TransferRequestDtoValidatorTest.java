package spaceurgent.banking.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spaceurgent.banking.dto.TransferRequestDto;
import spaceurgent.banking.exception.ValidationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;

@ExtendWith(MockitoExtension.class)
class TransferRequestDtoValidatorTest {
    @Mock
    private AccountNumberValidator accountNumberValidator;
    @InjectMocks
    private TransferRequestDtoValidator transferRequestDtoValidator;

    @Test
    void validate_ok() {
        final var sourceAccountNumber = "26000000000001";
        final var targetAccountNumber = "26000000000002";
        final var amount = BigDecimal.valueOf(100);
        final var transferRequestDto = new TransferRequestDto(sourceAccountNumber, targetAccountNumber, amount);
        assertDoesNotThrow(() -> transferRequestDtoValidator.validate(transferRequestDto));
    }

    @Test
    void validate_withSourceAccountNumberEqualsToTargetAccountNumber() {
        final var amount = BigDecimal.valueOf(100);
        final var transferRequestDto = new TransferRequestDto(TEST_ACCOUNT_NUMBER, TEST_ACCOUNT_NUMBER, amount);
        final var exception = assertThrows(
                ValidationException.class,
                () -> transferRequestDtoValidator.validate(transferRequestDto)
        );
        assertEquals(
                "Invalid transfer request. Source and target account numbers can't be equal",
                exception.getMessage()
        );
    }

    @Test
    void validate_withNullTransferRequest() {
        assertThrows(NullPointerException.class, () -> transferRequestDtoValidator.validate(null));
    }
}