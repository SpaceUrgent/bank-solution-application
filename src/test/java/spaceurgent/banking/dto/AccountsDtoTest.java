package spaceurgent.banking.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static spaceurgent.banking.TestUtils.randomAccounts;

class AccountsDtoTest {

    @Test
    @DisplayName("Create from accounts - OK")
    void fromAccounts_ok() {
        final var accounts = randomAccounts();
        final var accountsDto = AccountsDto.from(accounts);
        final var accountDtoList = accountsDto.data();
        assertEquals(accounts.size(), accountDtoList.size(), "Accounts size doesn't match");
        for (int i = 0; i < accounts.size(); i++) {
            final var currentAccount = accounts.get(i);
            final var currentAccountDto = accountDtoList.get(i);
            assertEquals(currentAccount.getNumber(), currentAccountDto.number(), "Account number doesn't match");
            assertEquals(currentAccount.getCurrency(), currentAccountDto.currency(), "Account number doesn't match");
        }
    }
}