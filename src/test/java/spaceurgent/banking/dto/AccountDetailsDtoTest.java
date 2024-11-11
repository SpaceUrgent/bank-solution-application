package spaceurgent.banking.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spaceurgent.banking.model.Account;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;

class AccountDetailsDtoTest {

    @Test
    @DisplayName("Create from account - OK")
    void fromAccount_ok() {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        final var accountDetailsDto = AccountDetailsDto.from(account);
        assertEquals(TEST_ACCOUNT_NUMBER, accountDetailsDto.number(), "Account number doesn't match");
        assertEquals(account.getCurrency(), accountDetailsDto.currency(), "Account currency doesn't match");
        assertEquals(account.getBalance(), accountDetailsDto.balance(), "Account balance doesn't match");
    }
}