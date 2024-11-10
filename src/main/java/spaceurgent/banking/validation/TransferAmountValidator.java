package spaceurgent.banking.validation;

import org.springframework.stereotype.Component;
import spaceurgent.banking.exception.ValidationException;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;
import static spaceurgent.banking.utils.AmountUtils.isNegativeOrZero;

@Component
public class TransferAmountValidator implements Validator<BigDecimal> {

    @Override
    public void validate(BigDecimal transferAmount) {
        requireNonNull(transferAmount, "Transfer amount is required");
        if (isNegativeOrZero(transferAmount)) {
            throw new ValidationException("Invalid transfer amount. Amount must be grater than 0");
        }
    }
}
