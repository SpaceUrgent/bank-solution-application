package spaceurgent.banking.dto;

import spaceurgent.banking.model.Account;
import spaceurgent.banking.model.Currency;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

public record AccountDetailsDto(String number,
                                Currency currency,
                                BigDecimal balance) {
    public AccountDetailsDto {
        requireNonNull(number, "Number is required");
        requireNonNull(currency, "Currency is required");
        requireNonNull(balance, "Balance is required");
    }

    public static AccountDetailsDto from(Account account) {
        requireNonNull(account, "Account is required");
        return new AccountDetailsDto(account.getNumber(), account.getCurrency(), account.getBalance());
    }
}
