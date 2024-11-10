package spaceurgent.banking.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import spaceurgent.banking.TestUtils;
import spaceurgent.banking.dto.AccountsDto;
import spaceurgent.banking.dto.TransferRequestDto;
import spaceurgent.banking.exception.AccountNotFoundException;
import spaceurgent.banking.exception.InvalidAmountException;
import spaceurgent.banking.model.Account;
import spaceurgent.banking.service.AccountService;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spaceurgent.banking.TestConstants.DEFAULT_SOURCE_ACCOUNT_NUMBER;
import static spaceurgent.banking.TestConstants.DEFAULT_TARGET_ACCOUNT_NUMBER;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;
import static spaceurgent.banking.api.AccountControllerTest.ErrorTimestampMatcher.validErrorTimestamp;
import static spaceurgent.banking.api.ApiConstants.AMOUNT_PARAMETER_NAME;
import static spaceurgent.banking.api.ApiConstants.BALANCE_PARAMETER_NAME;
import static spaceurgent.banking.api.ApiConstants.TARGET_ACCOUNT_NUMBER_PARAMETER_NAME;

@WebMvcTest
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountService accountService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Create account with balance param returns 201")
    void createAccount_withBalanceParam_returns201() throws Exception {
        final var balance = BigDecimal.valueOf(100.50);
        final var account = new Account(TEST_ACCOUNT_NUMBER, balance);
        doReturn(account).when(accountService).createAccount(eq(balance));
        final var apiActionResult = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(BALANCE_PARAMETER_NAME, balance.toString()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        assertAccountDetailsViewMatchAccount(apiActionResult, account);
    }

    @Test
    @DisplayName("Create account without balance param returns 201")
    void createAccount_withoutBalanceParam_returns201() throws Exception {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        doReturn(account).when(accountService).createAccount(argThat(new ZeroBalanceMatcher()));
        final var apiActionResult = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        assertAccountDetailsViewMatchAccount(apiActionResult, account);
    }

    @Test
    @DisplayName("Create account with negative balance returns 400")
    void createAccount_withNegativeBalanceParam_returns400() throws Exception {
        final var negativeBalance = BigDecimal.valueOf(-100);
        final var errorMessage = "Balance can't be negative";
        doThrow(new InvalidAmountException(errorMessage)).when(accountService).createAccount(argThat(new NegativeBalanceMatcher()));
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(BALANCE_PARAMETER_NAME, negativeBalance.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp", validErrorTimestamp()))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/accounts"));
    }

    @Test
    @DisplayName("Get accounts returns 200")
    void getAccounts_returns200() throws Exception {
        final var accounts = TestUtils.randomAccounts();
        doReturn(accounts).when(accountService).findAccounts();
        final var responseBodyJson = mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var accountsDto = objectMapper.readValue(responseBodyJson, AccountsDto.class);
        final var accountDtoList = accountsDto.data();
        assertEquals(accounts.size(), accountDtoList.size());
        for (int i = 0; i < accounts.size(); i++) {
            final var currentAccount = accounts.get(i);
            final var currentAccountDto = accountDtoList.get(i);
            assertEquals(currentAccount.getNumber(), currentAccountDto.number());
            assertEquals(currentAccount.getCurrency(), currentAccountDto.currency());
        }
    }

    @Test
    @DisplayName("Get account returns 200")
    void getAccount_returns200() throws Exception {
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO);
        doReturn(account).when(accountService).findAccount(eq(account.getNumber()));
        final var apiActionResult = mockMvc.perform(get("/api/accounts/{accountNumber}", account.getNumber()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        assertAccountDetailsViewMatchAccount(apiActionResult, account);
    }

    @Test
    @DisplayName("Get account with non-existing account number returns 404")
    void getAccount_withNonExistingAccountNumber_returns404() throws Exception {
        final var errorMessage = "Account not found";
        doThrow(new AccountNotFoundException(errorMessage)).when(accountService).findAccount(any());
        mockMvc.perform(get("/api/accounts/{accountNumber}", TEST_ACCOUNT_NUMBER))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp", validErrorTimestamp()))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/accounts/%s".formatted(TEST_ACCOUNT_NUMBER)));
    }

    @Test
    @DisplayName("Deposit to account returns 200")
    void depositToAccount_returns200() throws Exception {
        final var depositAmount = BigDecimal.valueOf(100.00);
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.valueOf(0.00));
        account.deposit(depositAmount);
        doReturn(account).when(accountService).depositToAccount(eq(account.getNumber()), eq(depositAmount));
        final var apiActionResult = mockMvc.perform(post("/api/accounts/{accountNumber}/deposit", account.getNumber())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param(AMOUNT_PARAMETER_NAME, depositAmount.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        assertAccountDetailsViewMatchAccount(apiActionResult, account);
    }

    @Test
    @DisplayName("Deposit to account without amount parameter returns 400")
    void depositToAccount_withoutAmount_returns400() throws Exception {
        mockMvc.perform(post("/api/accounts/{accountNumber}/deposit", TEST_ACCOUNT_NUMBER)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp", validErrorTimestamp()))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Required parameter 'amount' is not present."))
                .andExpect(jsonPath("$.path").value("/api/accounts/%s/deposit".formatted(TEST_ACCOUNT_NUMBER)));
    }

    @Test
    @DisplayName("Withdraw from account returns 200")
    void withdrawFromAccount_returns200() throws Exception {
        final var withdrawAmount = BigDecimal.valueOf(10);
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.valueOf(100));
        doReturn(account).when(accountService).withdrawFromAccount(eq(account.getNumber()), eq(withdrawAmount));
        final var apiActionResult = mockMvc.perform(post("/api/accounts/{accountNumber}/withdraw", account.getNumber())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(AMOUNT_PARAMETER_NAME, withdrawAmount.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        assertAccountDetailsViewMatchAccount(apiActionResult, account);
    }

    @Test
    @DisplayName("Withdraw from account without amount parameter returns 400")
    void withdrawFromAccount_withoutAmount_returns400() throws Exception {
        final var withdrawAmount = BigDecimal.valueOf(10);
        final var account = new Account(TEST_ACCOUNT_NUMBER, BigDecimal.valueOf(100));
        doReturn(account).when(accountService).withdrawFromAccount(eq(TEST_ACCOUNT_NUMBER), eq(withdrawAmount));
        mockMvc.perform(post("/api/accounts/{accountNumber}/withdraw", TEST_ACCOUNT_NUMBER))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp", validErrorTimestamp()))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Required parameter 'amount' is not present."))
                .andExpect(jsonPath("$.path").value("/api/accounts/%s/withdraw".formatted(TEST_ACCOUNT_NUMBER)));
    }

    @Test
    @DisplayName("Transfer to account returns 200")
    void transferToAccount_ok() throws Exception {
        final var transferAmount = BigDecimal.valueOf(10);
        final var expectedTransferRequest = new TransferRequestDto(
                DEFAULT_SOURCE_ACCOUNT_NUMBER,
                DEFAULT_TARGET_ACCOUNT_NUMBER,
                transferAmount
        );
        final var sourceAccount = new Account(DEFAULT_SOURCE_ACCOUNT_NUMBER, BigDecimal.valueOf(100));
        doReturn(sourceAccount).when(accountService).transferToAccount(eq(expectedTransferRequest));
        final var apiActionResult = mockMvc.perform(post("/api/accounts/{accountNumber}/transfer", DEFAULT_SOURCE_ACCOUNT_NUMBER)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(TARGET_ACCOUNT_NUMBER_PARAMETER_NAME, DEFAULT_TARGET_ACCOUNT_NUMBER)
                        .param(AMOUNT_PARAMETER_NAME, transferAmount.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        assertAccountDetailsViewMatchAccount(apiActionResult, sourceAccount);
    }

    @Test
    @DisplayName("Transfer to account without target account number returns 400")
    void transferToAccount_withoutTargetAccountNumber_returns400() throws Exception {
        mockMvc.perform(post("/api/accounts/{accountNumber}/transfer", DEFAULT_SOURCE_ACCOUNT_NUMBER)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(AMOUNT_PARAMETER_NAME, BigDecimal.valueOf(10).toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp", validErrorTimestamp()))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Required parameter 'targetAccountNumber' is not present."))
                .andExpect(jsonPath("$.path").value("/api/accounts/%s/transfer".formatted(DEFAULT_SOURCE_ACCOUNT_NUMBER)));
    }

    @Test
    @DisplayName("Transfer to account without amount parameter returns 400")
    void transferToAccount_withoutAmount_returns400() throws Exception {
        mockMvc.perform(post("/api/accounts/{accountNumber}/transfer", DEFAULT_SOURCE_ACCOUNT_NUMBER)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(TARGET_ACCOUNT_NUMBER_PARAMETER_NAME, DEFAULT_TARGET_ACCOUNT_NUMBER))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp", validErrorTimestamp()))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Required parameter 'amount' is not present."))
                .andExpect(jsonPath("$.path").value("/api/accounts/%s/transfer".formatted(DEFAULT_SOURCE_ACCOUNT_NUMBER)));
    }

    private void assertAccountDetailsViewMatchAccount(ResultActions resultActions, Account account) throws Exception {
        resultActions
                .andExpect(jsonPath("$.number").value(account.getNumber()))
                .andExpect(jsonPath("$.currency").value(account.getCurrency().name()))
                .andExpect(jsonPath("$.balance").value(account.getBalance().doubleValue()));
    }

    private static class ZeroBalanceMatcher implements ArgumentMatcher<BigDecimal> {

        @Override
        public boolean matches(BigDecimal argument) {
            return BigDecimal.ZERO.compareTo(argument) == 0;
        }
    }

    private static class NegativeBalanceMatcher implements ArgumentMatcher<BigDecimal> {

        @Override
        public boolean matches(BigDecimal argument) {
            return BigDecimal.ZERO.compareTo(argument) > 0;
        }
    }

    static class ErrorTimestampMatcher extends TypeSafeDiagnosingMatcher<String> {

        static Matcher<String> validErrorTimestamp() {
            return new ErrorTimestampMatcher();
        }

        @Override
        protected boolean matchesSafely(String errorTimestampString, Description description) {
            Instant errorTimestamp;
            try {
                errorTimestamp = Instant.parse(errorTimestampString);
            } catch (Exception e) {
                description.appendText("failed to parse timestamp from ").appendValue(errorTimestampString);
                return false;
            }
            if (errorTimestamp.isBefore(Instant.now())) {
                return true;
            } else {
                description.appendText("error timestamp must be less then current timestamp");
                return false;
            }
        }

        @Override
        public void describeTo(Description description) {
        }
    }
}