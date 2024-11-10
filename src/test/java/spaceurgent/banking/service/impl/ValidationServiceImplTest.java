package spaceurgent.banking.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spaceurgent.banking.dto.TransferRequestDto;
import spaceurgent.banking.validation.Validator;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static spaceurgent.banking.TestConstants.DEFAULT_SOURCE_ACCOUNT_NUMBER;
import static spaceurgent.banking.TestConstants.DEFAULT_TARGET_ACCOUNT_NUMBER;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {
    @Mock
    private Validator<String> accountNumberValidator;
    @Mock
    private Validator<TransferRequestDto> transferRequestDtoValidator;
    @Mock
    private Validator<BigDecimal> balanceAmountValidator;
    @Mock
    private Validator<BigDecimal> transferAmountValidator;

    private ValidationServiceImpl validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationServiceImpl(
                accountNumberValidator,
                transferRequestDtoValidator,
                balanceAmountValidator,
                transferAmountValidator
        );
    }

    @Test
    void validateAccountNumber() {
        validationService.validateAccountNumber(TEST_ACCOUNT_NUMBER);
        verify(accountNumberValidator).validate(eq(TEST_ACCOUNT_NUMBER));
    }

    @Test
    void validateTransferRequestDto() {
        final var transferRequestDto = new TransferRequestDto(
                DEFAULT_SOURCE_ACCOUNT_NUMBER,
                DEFAULT_TARGET_ACCOUNT_NUMBER,
                BigDecimal.valueOf(100)
        );
        validationService.validateTransferRequestDto(transferRequestDto);
        verify(transferRequestDtoValidator).validate(eq(transferRequestDto));
    }

    @Test
    void validateBalanceAmount() {
        final var balanceAmount = BigDecimal.valueOf(150);
        validationService.validateBalanceAmount(balanceAmount);
        verify(balanceAmountValidator).validate(eq(balanceAmount));
    }

    @Test
    void validateTransferAmount() {
        final var transferAmount = BigDecimal.valueOf(200);
        validationService.validateTransferAmount(transferAmount);
        verify(transferAmountValidator).validate(eq(transferAmount));
    }
}