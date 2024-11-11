package spaceurgent.banking.validation;

import org.springframework.stereotype.Component;
import spaceurgent.banking.Constants;
import spaceurgent.banking.exception.ValidationException;

import static java.util.Objects.requireNonNull;

@Component
public class AccountNumberValidator implements Validator<String> {

    @Override
    public void validate(String accountNumber) {
        requireNonNull(accountNumber, "Account number is required");
        if (!accountNumber.matches(Constants.ACCOUNT_NUMBER_REGEX)) {
            throw new ValidationException("Invalid account number");
        }
    }
}
