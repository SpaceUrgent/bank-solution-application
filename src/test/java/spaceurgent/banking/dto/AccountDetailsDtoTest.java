package spaceurgent.banking.dto;

import org.junit.jupiter.api.Test;
import spaceurgent.banking.model.Account;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;

class AccountDetailsDtoTest {

    @Test
    void fromAccount() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        final var accountDetailsDto = AccountDetailsDto.from(account);
        assertEquals(TEST_ACCOUNT_NUMBER, accountDetailsDto.number());
        assertEquals(account.getCurrency(), accountDetailsDto.currency());
        assertEquals(account.getBalance(), accountDetailsDto.balance());
    }
}