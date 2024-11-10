package spaceurgent.banking;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.ResultActions;
import spaceurgent.banking.model.Account;
import spaceurgent.banking.model.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public final class TestUtils {
    private TestUtils() {
    }

    public final static Currency DEFAULT_CURRENCY = Currency.UAH;

    public static List<Account> randomAccounts() {
        return IntStream.range(0, 10)
                .mapToObj(index -> new Account(String.valueOf(index), BigDecimal.valueOf(index)))
                .toList();
    }

    public static Answer<?> returnInputAnswer() {
        return (Answer<Object>) invocation -> invocation.getArguments()[0];
    }

    public static void assertAccountDetailsViewMatchAccount(ResultActions apiCallResult, Account account) throws Exception {
        apiCallResult
                .andExpect(jsonPath("$.number").value(account.getNumber()))
                .andExpect(jsonPath("$.currency").value(account.getCurrency().name()))
                .andExpect(jsonPath("$.balance").value(account.getBalance().doubleValue()));
    }

    static ErrorTimestampMatcher errorTimestampMatcher() {
        return new ErrorTimestampMatcher();
    }

    static class ErrorTimestampMatcher extends TypeSafeDiagnosingMatcher<String> {

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
