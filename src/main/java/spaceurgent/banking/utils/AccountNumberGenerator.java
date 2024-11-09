package spaceurgent.banking.utils;

import java.util.concurrent.atomic.AtomicLong;

public class AccountNumberGenerator {
    private AccountNumberGenerator() {
    }

    private static final AtomicLong ACCOUNT_NUMBER_SEQUENCE = new AtomicLong(1);
    private static final String UAH_ACCOUNT_NUMBER_FORMAT = "2600%010d";

    public static String nextAccountNumber() {
        return UAH_ACCOUNT_NUMBER_FORMAT.formatted(ACCOUNT_NUMBER_SEQUENCE.getAndIncrement());
    }
}
