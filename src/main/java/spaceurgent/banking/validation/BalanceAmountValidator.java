package spaceurgent.banking.validation;

import org.springframework.stereotype.Component;
import spaceurgent.banking.exception.ValidationException;

import java.math.BigDecimal;
import java.util.Objects;

import static spaceurgent.banking.utils.AmountUtils.isNegative;

@Component
public class BalanceAmountValidator implements Validator<BigDecimal> {

    @Override
    public void validate(BigDecimal balanceAmount) {
        Objects.requireNonNull(balanceAmount, "Balance amount is required");
        if (isNegative(balanceAmount)) {
            throw new ValidationException("Invalid balance. Balance must be equal or greater than 0");
        }
    }
}
