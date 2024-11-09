package spaceurgent.banking.dto;

import org.junit.jupiter.api.Test;
import spaceurgent.banking.model.Account;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;

class AccountDtoTest {

    @Test
    void fromAccount() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        final var accountDto = AccountDto.from(account);
        assertEquals(TEST_ACCOUNT_NUMBER, accountDto.number());
        assertEquals(account.getCurrency(), accountDto.currency());
        assertEquals(account.getBalance(), accountDto.balance());
    }
}