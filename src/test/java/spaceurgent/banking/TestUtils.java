package spaceurgent.banking;

import spaceurgent.banking.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

public class TestUtils {
    private TestUtils() {
    }

    public static List<Account> randomAccounts() {
        return IntStream.range(0, 10)
                .mapToObj(index -> new Account(String.valueOf(index), BigDecimal.valueOf(index)))
                .toList();
    }
}
