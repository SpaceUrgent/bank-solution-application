package spaceurgent.banking.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spaceurgent.banking.repository.AccountRepository;
import spaceurgent.banking.utils.AccountNumberGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountNumberGenerator accountNumberGenerator;
    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void createAccount() {
        final var initialBalance = BigDecimal.ZERO.setScale(2, RoundingMode.FLOOR);
        doReturn(TEST_ACCOUNT_NUMBER).when(accountNumberGenerator).nextAccountNumber();
        doAnswer(invocation -> invocation.getArguments()[0]).when(accountRepository).save(any());
        final var created = accountService.createAccount(initialBalance);
        assertEquals(TEST_ACCOUNT_NUMBER, created.getNumber(), "Created account doesn't match expected");
        assertEquals(initialBalance, created.getBalance(), "Initial balanced doesn't match expected");
    }
}