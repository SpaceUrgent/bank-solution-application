package spaceurgent.banking.service;

import spaceurgent.banking.dto.TransferRequestDto;

import java.math.BigDecimal;

public interface ValidationService {

    void validateAccountNumber(String accountNumber);

    void validateTransferRequestDto(TransferRequestDto transferRequestDto);

    void validateBalanceAmount(BigDecimal balanceAmount);

    void validateTransferAmount(BigDecimal transferAmount);
}
