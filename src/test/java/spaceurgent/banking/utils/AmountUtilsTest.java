package spaceurgent.banking.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spaceurgent.banking.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class AmountUtilsTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.001, 0.00111, 0.009})
    @DisplayName("Round - OK")
    void round_ok(Double doubleValue) {
        final var roundTarget = BigDecimal.valueOf(doubleValue);
        final var expected = roundTarget.setScale(Constants.DEFAULT_AMOUNT_SCALE, RoundingMode.FLOOR);
        assertEquals(expected, AmountUtils.round(roundTarget));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.001, -0.00111, -0.009, -1, -100})
    @DisplayName("Is negative returns true")
    void isNegative_returnsTrue(Double doubleValue) {
        assertTrue(AmountUtils.isNegative(BigDecimal.valueOf(doubleValue)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 0.001, 0.009, 1, 100})
    @DisplayName("Is negative returns false")
    void isNegative_returnsFalse(Double doubleValue) {
        assertFalse(AmountUtils.isNegative(BigDecimal.valueOf(doubleValue)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 0.001, 0.009, -0.001, -0.00111, -0.009, -1, -100})
    @DisplayName("Is negative or 0 returns true")
    void isNegativeOrZero_returnsTrue(Double doubleValue) {
        assertTrue(AmountUtils.isNegativeOrZero(BigDecimal.valueOf(doubleValue)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 1, 100})
    @DisplayName("Is negative or 0 returns false")
    void isNegativeOrZero_returnsFalse(Double doubleValue) {
        assertFalse(AmountUtils.isNegativeOrZero(BigDecimal.valueOf(doubleValue)));
    }
}