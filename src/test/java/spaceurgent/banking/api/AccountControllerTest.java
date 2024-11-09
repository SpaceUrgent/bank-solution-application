package spaceurgent.banking.api;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import spaceurgent.banking.exception.InvalidAmountException;
import spaceurgent.banking.model.Account;
import spaceurgent.banking.service.AccountService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spaceurgent.banking.TestConstants.TEST_ACCOUNT_NUMBER;
import static spaceurgent.banking.api.AccountControllerTest.ErrorTimestampMatcher.validErrorTimestamp;

@WebMvcTest
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountService accountService;

    @Test
    void createAccount_withBalanceParam() throws Exception {
        final var accountNumber = new Random().nextLong();
        final var balance = BigDecimal.valueOf(100.50);
        final var spiedAccount = spy(new Account(TEST_ACCOUNT_NUMBER, balance));
        doReturn(spiedAccount).when(accountService).createAccount(eq(balance));
        doReturn(accountNumber).when(spiedAccount).getId();
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("balance", balance.toString()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number").value(TEST_ACCOUNT_NUMBER))
                .andExpect(jsonPath("$.currency").value(spiedAccount.getCurrency().name()))
                .andExpect(jsonPath("$.balance").value(balance));
    }

    @Test
    void createAccount_withoutBalanceParam() throws Exception {
        final var accountNumber = new Random().nextLong();
        final var spiedAccount = spy(new Account(TEST_ACCOUNT_NUMBER, BigDecimal.valueOf(0.00)));
        doReturn(spiedAccount).when(accountService).createAccount(argThat(new ZeroBalanceMatcher()));
        doReturn(accountNumber).when(spiedAccount).getId();
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number").value(TEST_ACCOUNT_NUMBER))
                .andExpect(jsonPath("$.currency").value(spiedAccount.getCurrency().name()))
                .andExpect(jsonPath("$.balance").value(BigDecimal.valueOf(0.00).toString()));
    }

    @Test
    void createAccount_withNegativeBalanceParam() throws Exception {
        final var negativeBalance = BigDecimal.valueOf(-100);
        final var errorMessage = "Balance can't be negative";
        doThrow(new InvalidAmountException(errorMessage)).when(accountService).createAccount(argThat(new NegativeBalanceMatcher()));
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("balance", negativeBalance.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp", validErrorTimestamp()))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/accounts"));
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