package spaceurgent.banking.model;

import spaceurgent.banking.exception.UnsupportedCurrencyException;

import java.util.Arrays;

public enum Currency {
    UAH,
    USD,
    EUR;

    public static Currency of(String currencyAbbreviation) {
        assert currencyAbbreviation != null : "Currency abbreviation is required";
        return Arrays.stream(Currency.values())
                .filter(currency -> currency.name().equals(currencyAbbreviation))
                .findFirst()
                .orElseThrow(() -> new UnsupportedCurrencyException("Currency '%s' is not supported".formatted(currencyAbbreviation)));
    }
}
