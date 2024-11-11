package spaceurgent.banking.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class AccountNumberGenerator {
    public AccountNumberGenerator() {
    }

    private static final AtomicLong ACCOUNT_NUMBER_SEQUENCE = new AtomicLong(1);
    private static final String UAH_ACCOUNT_NUMBER_FORMAT = "2600%010d";

    public String nextAccountNumber() {
        return UAH_ACCOUNT_NUMBER_FORMAT.formatted(ACCOUNT_NUMBER_SEQUENCE.getAndIncrement());
    }
}
