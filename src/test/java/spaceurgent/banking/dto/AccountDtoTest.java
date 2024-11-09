package spaceurgent.banking.dto;

import org.junit.jupiter.api.Test;
import spaceurgent.banking.TestConstants;
import spaceurgent.banking.model.Account;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountDtoTest {

    @Test
    void fromAccount() {
        final var account = new Account(TestConstants.TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        AccountDto accountDto = AccountDto.from(account);
        assertEquals(account.getNumber(), accountDto.number());
        assertEquals(account.getCurrency(), accountDto.currency());
    }
}