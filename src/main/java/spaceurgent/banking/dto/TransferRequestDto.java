package spaceurgent.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import spaceurgent.banking.Constants;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

@Data
public class TransferRequestDto {
    @NotBlank
    @Pattern(
            regexp = Constants.ACCOUNT_NUMBER_REGEX,
            message = "Invalid source account number"
    )
    private String sourceAccountNumber;
    @NotBlank
    @Pattern(
            regexp = Constants.ACCOUNT_NUMBER_REGEX,
            message = "Invalid source account number"
    )
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
