package spaceurgent.banking.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spaceurgent.banking.dto.TransferRequestDto;
import spaceurgent.banking.exception.ValidationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static spaceurgent.banking.TestConstants.DEFAULT_SOURCE_ACCOUNT_NUMBER;
import static spaceurgent.banking.TestConstants.DEFAULT_TARGET_ACCOUNT_NUMBER;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;

@ExtendWith(MockitoExtension.class)
class TransferRequestDtoValidatorTest {
    @Mock
    private AccountNumberValidator accountNumberValidator;
    @InjectMocks
    private TransferRequestDtoValidator transferRequestDtoValidator;

    @Test
    @DisplayName("Validate - OK")
    void validate_ok() {
        final var amount = BigDecimal.valueOf(100);
        final var transferRequestDto = new TransferRequestDto(DEFAULT_SOURCE_ACCOUNT_NUMBER, DEFAULT_TARGET_ACCOUNT_NUMBER, amount);
        assertDoesNotThrow(() -> transferRequestDtoValidator.validate(transferRequestDto));
    }

    @Test
    @DisplayName("Validate with source account number equals to target throws")
    void validate_withSourceAccountNumberEqualsToTargetAccountNumber_throws() {
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
    @DisplayName("Validate with null transfer request - throws")
    void validate_withNullTransferRequest_throws() {
        assertThrows(NullPointerException.class, () -> transferRequestDtoValidator.validate(null));
    }
}