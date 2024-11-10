package spaceurgent.banking;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import spaceurgent.banking.model.Account;
import spaceurgent.banking.model.Currency;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

public class TestUtils {
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
}
