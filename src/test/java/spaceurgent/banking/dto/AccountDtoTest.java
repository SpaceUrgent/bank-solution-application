package spaceurgent.banking.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spaceurgent.banking.TestConstants;
import spaceurgent.banking.model.Account;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountDtoTest {

    @Test
    @DisplayName("Create from account - OK")
    void fromAccount_ok() {
        final var account = new Account(TestConstants.TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        AccountDto accountDto = AccountDto.from(account);
        assertEquals(account.getNumber(), accountDto.number(), "Account number doesn't match");
        assertEquals(account.getCurrency(), accountDto.currency(), "Account currency doesn't match");
    }
}