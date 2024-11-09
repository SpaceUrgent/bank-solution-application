package spaceurgent.banking.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spaceurgent.banking.model.Account;
import spaceurgent.banking.repository.AccountRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void createAccount() {
        final var initialBalance = BigDecimal.ZERO;
        final var accountExpected = new Account(TEST_ACCOUNT_NUMBER, initialBalance);
        doAnswer(invocation -> invocation.getArguments()[0]).when(accountRepository).save(any());
        final var created = accountService.createAccount(initialBalance);
        assertEquals(accountExpected, created, "Created account doesn't match expected");
    }
}