package spaceurgent.banking.dto;

import lombok.Data;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

@Data
public class TransferRequestDto {
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private BigDecimal amount;

    public TransferRequestDto(String sourceAccountNumber,
                              String targetAccountNumber,
                              BigDecimal amount) {
        this.sourceAccountNumber = requireNonNull(sourceAccountNumber, "Source account number is required");
        this.targetAccountNumber = requireNonNull(targetAccountNumber, "Target account number is required");
        this.amount = requireNonNull(amount, "Amount is required");
    }
}
