package spaceurgent.banking.validation;

import org.springframework.stereotype.Component;
import spaceurgent.banking.exception.ValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.util.Objects.requireNonNull;

@Component
public class TransferAmountValidator implements Validator<BigDecimal> {

    @Override
    public void validate(BigDecimal transferAmount) {
        requireNonNull(transferAmount, "Transfer amount is required");
        if (transferAmount.setScale(2, RoundingMode.FLOOR).compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Invalid transfer amount. Amount must be grater than 0");
        }
    }
}
