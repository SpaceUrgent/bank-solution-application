package spaceurgent.banking.model;

import spaceurgent.banking.exception.UnsupportedCurrencyException;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

public enum Currency {
    UAH;

    public static Currency of(String currencyAbbreviation) {
        requireNonNull(currencyAbbreviation, "Currency abbreviation is required");
        return Arrays.stream(Currency.values())
                .filter(currency -> currency.name().equals(currencyAbbreviation))
                .findFirst()
                .orElseThrow(() -> new UnsupportedCurrencyException("Currency '%s' is not supported".formatted(currencyAbbreviation)));
    }
}
