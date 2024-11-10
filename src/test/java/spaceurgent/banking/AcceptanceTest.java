package spaceurgent.banking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import spaceurgent.banking.dto.AccountDetailsDto;
import spaceurgent.banking.model.Account;
import spaceurgent.banking.model.Currency;
import spaceurgent.banking.repository.AccountRepository;

import java.math.BigDecimal;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spaceurgent.banking.AcceptanceTest.ErrorTimestampMatcher.validErrorTimestamp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AcceptanceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void createNewAccount() throws Exception {
        final var initialBalance = BigDecimal.valueOf(100);
        final var jsonResponseBody = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("balance", initialBalance.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").exists())
                .andExpect(jsonPath("$.currency").value(Currency.UAH.name()))
                .andExpect(jsonPath("$.balance").value(initialBalance.doubleValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var accountDetailsDto = objectMapper.readValue(jsonResponseBody, AccountDetailsDto.class);
        final var accountNumber = accountDetailsDto.number();

        mockMvc.perform(get("/api/accounts/{accountNumber}", accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(accountNumber))
                .andExpect(jsonPath("$.currency").value(Currency.UAH.name()))
                .andExpect(jsonPath("$.balance").value(initialBalance.doubleValue()));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].number").value(accountNumber))
                .andExpect(jsonPath("$.data[0].currency").value(Currency.UAH.name()));
    }

    @Test
    void depositToAccount() throws Exception {
        final var accountToDeposit = accountRepository.save(new Account(TestConstants.TEST_ACCOUNT_NUMBER, BigDecimal.ZERO));
        final var depositAmount = BigDecimal.valueOf(100);
        final var expectedBalance = accountToDeposit.getBalance().add(depositAmount);
        mockMvc.perform(post("/api/accounts/{accountNumber}/deposit", accountToDeposit.getNumber())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("amount", depositAmount.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number").value(accountToDeposit.getNumber()))
                .andExpect(jsonPath("$.currency").value(accountToDeposit.getCurrency().name()))
                .andExpect(jsonPath("$.balance").value(expectedBalance.doubleValue()));

        mockMvc.perform(get("/api/accounts/{accountNumber}", accountToDeposit.getNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(accountToDeposit.getNumber()))
                .andExpect(jsonPath("$.currency").value(accountToDeposit.getCurrency().name()))
                .andExpect(jsonPath("$.balance").value(expectedBalance.doubleValue()));
    }

    @Test
    void withdrawFromAccount() throws Exception {
        final var initialBalance = BigDecimal.valueOf(100);
        final var accountToWithdraw = accountRepository.save(new Account(TestConstants.TEST_ACCOUNT_NUMBER, initialBalance));
        final var withdrawAmount = BigDecimal.valueOf(10);
        final var expectedBalance = accountToWithdraw.getBalance().subtract(withdrawAmount);
        mockMvc.perform(post("/api/accounts/{accountNumber}/withdraw", accountToWithdraw.getNumber())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("amount", withdrawAmount.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number").value(accountToWithdraw.getNumber()))
                .andExpect(jsonPath("$.currency").value(accountToWithdraw.getCurrency().name()))
                .andExpect(jsonPath("$.balance").value(expectedBalance.doubleValue()));

        mockMvc.perform(get("/api/accounts/{accountNumber}", accountToWithdraw.getNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(accountToWithdraw.getNumber()))
                .andExpect(jsonPath("$.currency").value(accountToWithdraw.getCurrency().name()))
                .andExpect(jsonPath("$.balance").value(expectedBalance.doubleValue()));
    }

    @Test
    void withdrawFromAccount_withAmountExceedsBalance() throws Exception {
        final var initialBalance = BigDecimal.valueOf(10);
        final var accountToWithdraw = accountRepository.save(new Account(TestConstants.TEST_ACCOUNT_NUMBER, initialBalance));
        final var withdrawAmount = BigDecimal.valueOf(100);
        mockMvc.perform(post("/api/accounts/{accountNumber}/withdraw", accountToWithdraw.getNumber())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("amount", withdrawAmount.toString()))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.timestamp").exists())
               .andExpect(jsonPath("$.timestamp", validErrorTimestamp()))
               .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
               .andExpect(jsonPath("$.message").value("Withdraw amount exceeds balance"))
               .andExpect(jsonPath("$.path").value("/api/accounts/%s/withdraw".formatted(accountToWithdraw.getNumber())));

       mockMvc.perform(get("/api/accounts/{accountNumber}", accountToWithdraw.getNumber()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.number").value(accountToWithdraw.getNumber()))
               .andExpect(jsonPath("$.currency").value(accountToWithdraw.getCurrency().name()))
               .andExpect(jsonPath("$.balance").value(accountToWithdraw.getBalance().doubleValue()));
    }

    @Test
    void transferToAccount_ok() throws Exception {
        final var sourceAccount = accountRepository.save(new Account("26000000000001", BigDecimal.valueOf(100)));
        final var targetAccount = accountRepository.save(new Account("26000000000002", BigDecimal.ZERO));
        final var transferAmount = BigDecimal.valueOf(50);
        final var expectedSourceAccountBalance = sourceAccount.getBalance().subtract(transferAmount);
        final var expectedTargetAccountBalance = targetAccount.getBalance().add(transferAmount);
        mockMvc.perform(post("/api/accounts/{sourceAccountNumber}/transfer", sourceAccount.getNumber())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("targetAccountNumber", targetAccount.getNumber())
                        .param("amount", transferAmount.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number").value(sourceAccount.getNumber()))
                .andExpect(jsonPath("$.currency").value(sourceAccount.getCurrency().name()))
                .andExpect(jsonPath("$.balance").value(expectedSourceAccountBalance.doubleValue()));

        mockMvc.perform(get("/api/accounts/{accountNumber}", sourceAccount.getNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(sourceAccount.getNumber()))
                .andExpect(jsonPath("$.currency").value(sourceAccount.getCurrency().name()))
                .andExpect(jsonPath("$.balance").value(expectedSourceAccountBalance.doubleValue()));

        mockMvc.perform(get("/api/accounts/{accountNumber}", targetAccount.getNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(targetAccount.getNumber()))
                .andExpect(jsonPath("$.currency").value(targetAccount.getCurrency().name()))
                .andExpect(jsonPath("$.balance").value(expectedTargetAccountBalance.doubleValue()));
    }

    @Test
    void transferToAccount_withAmountExceedsSourceAccountBalance() throws Exception {
        final var sourceAccount = accountRepository.save(new Account("26000000000001", BigDecimal.ZERO));
        final var targetAccount = accountRepository.save(new Account("26000000000002", BigDecimal.ZERO));
        final var transferAmount = BigDecimal.valueOf(100);
        mockMvc.perform(post("/api/accounts/{sourceAccountNumber}/transfer", sourceAccount.getNumber())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("targetAccountNumber", targetAccount.getNumber())
                        .param("amount", transferAmount.toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp", validErrorTimestamp()))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
//                TODO: to turn back to error message
                .andExpect(jsonPath("$.message").value("Withdraw amount exceeds balance"))
                .andExpect(jsonPath("$.path").value("/api/accounts/%s/transfer".formatted(sourceAccount.getNumber())));

        mockMvc.perform(get("/api/accounts/{accountNumber}", sourceAccount.getNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(sourceAccount.getNumber()))
                .andExpect(jsonPath("$.currency").value(sourceAccount.getCurrency().name()))
                .andExpect(jsonPath("$.balance").value(sourceAccount.getBalance().doubleValue()));

        mockMvc.perform(get("/api/accounts/{accountNumber}", targetAccount.getNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(targetAccount.getNumber()))
                .andExpect(jsonPath("$.currency").value(targetAccount.getCurrency().name()))
                .andExpect(jsonPath("$.balance").value(targetAccount.getBalance().doubleValue()));
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
