package spaceurgent.banking.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spaceurgent.banking.exception.UnsupportedCurrencyException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyTest {

    @ParameterizedTest
    @ValueSource(strings = {"UAH", "USD", "EUR"})
    void currencyFromAbbreviation_withSupportedAbbreviations(String currencyAbbreviation) {
        assertEquals(currencyAbbreviation, Currency.of(currencyAbbreviation).name());
    }

    @Test
    void currencyFromAbbreviation_withUnsupportedAbbreviations() {
        final var randomString = UUID.randomUUID().toString();
        final var exception = assertThrows(
                UnsupportedCurrencyException.class,
                () -> Currency.of(randomString)
        );
        assertEquals("Currency '%s' is not supported".formatted(randomString), exception.getMessage());
    }
}