package spaceurgent.banking.dto;

import spaceurgent.banking.model.Account;

import java.util.List;
import java.util.Objects;

public record AccountsDto(List<AccountDto> data) {
    public AccountsDto {
        Objects.requireNonNull(data, "Account dto list is required");
    }

    public static AccountsDto from(List<Account> accounts) {
        return new AccountsDto(accounts.stream().map(AccountDto::from).toList());
    }
}
