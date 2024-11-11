package spaceurgent.banking.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import spaceurgent.banking.dto.TransferRequestDto;
import spaceurgent.banking.service.ValidationService;
import spaceurgent.banking.validation.Validator;

import java.math.BigDecimal;

@Service
public class ValidationServiceImpl implements ValidationService {
    private final Validator<String> accountNumberValidator;
    private final Validator<TransferRequestDto> transferRequestDtoValidator;
    private final Validator<BigDecimal> balanceAmountValidator;
    private final Validator<BigDecimal> transferAmountValidator;

    public ValidationServiceImpl(Validator<String> accountNumberValidator,
                                 Validator<TransferRequestDto> transferRequestDtoValidator,
                                 @Qualifier("balanceAmountValidator")
                                 Validator<BigDecimal> balanceAmountValidator,
                                 @Qualifier("transferAmountValidator")
                                 Validator<BigDecimal> transferAmountValidator) {
        this.accountNumberValidator = accountNumberValidator;
        this.transferRequestDtoValidator = transferRequestDtoValidator;
        this.balanceAmountValidator = balanceAmountValidator;
        this.transferAmountValidator = transferAmountValidator;
    }

    public void validateAccountNumber(String accountNumber) {
        this.accountNumberValidator.validate(accountNumber);
    }

    public void validateTransferRequestDto(TransferRequestDto transferRequestDto) {
        this.transferRequestDtoValidator.validate(transferRequestDto);
    }

    public void validateBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmountValidator.validate(balanceAmount);
    }

    public void validateTransferAmount(BigDecimal transferAmount) {
        this.transferAmountValidator.validate(transferAmount);
    }
}
