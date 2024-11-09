package spaceurgent.banking.dto;

import org.junit.jupiter.api.Test;
import spaceurgent.banking.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class AccountsDtoTest {

    @Test
    void fromAccounts() {
        final var accounts = randomAccounts();
        final var accountsDto = AccountsDto.from(accounts);
        final var accountDtoList = accountsDto.data();
        assertEquals(accounts.size(), accountDtoList.size());
        for (int i = 0; i < accounts.size(); i++) {
            final var currentAccount = accounts.get(i);
            final var currentAccountDto = accountDtoList.get(i);
            assertEquals(currentAccount.getNumber(), currentAccountDto.number());
            assertEquals(currentAccount.getCurrency(), currentAccountDto.currency());
        }
    }

    private List<Account> randomAccounts() {
        return IntStream.range(0, 10)
                .mapToObj(index -> new Account(String.valueOf(index), BigDecimal.valueOf(index)))
                .toList();
    }
}