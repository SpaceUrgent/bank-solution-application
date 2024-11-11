package spaceurgent.banking.dto;

import spaceurgent.banking.model.Account;
import spaceurgent.banking.model.Currency;

import static java.util.Objects.requireNonNull;

public record AccountDto(String number, Currency currency) {

    public AccountDto {
        requireNonNull(number, "Number is required");
        requireNonNull(currency, "Currency is required");
    }

    public static AccountDto from(Account account) {
        return new AccountDto(account.getNumber(), account.getCurrency());
    }
}
