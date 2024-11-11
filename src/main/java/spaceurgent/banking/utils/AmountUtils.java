package spaceurgent.banking.utils;

import spaceurgent.banking.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AmountUtils {

    public static BigDecimal round(BigDecimal value) {
        return value.setScale(Constants.DEFAULT_AMOUNT_SCALE, RoundingMode.FLOOR);
    }

    public static boolean isNegative(BigDecimal value) {
        return round(value).compareTo(BigDecimal.ZERO) < 0;
    }

    public static boolean isNegativeOrZero(BigDecimal value) {
        return round(value).compareTo(BigDecimal.ZERO) <= 0;
    }
}
