package spaceurgent.banking.validation;

import org.springframework.stereotype.Component;
import spaceurgent.banking.exception.ValidationException;

import java.math.BigDecimal;
import java.util.Objects;

@Component
public class BalanceAmountValidator implements Validator<BigDecimal> {

    @Override
    public void validate(BigDecimal balanceAmount) {
        Objects.requireNonNull(balanceAmount, "Balance amount is required");
        if (balanceAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Invalid balance. Balance must be equal or greater than 0");
        }
    }
}
