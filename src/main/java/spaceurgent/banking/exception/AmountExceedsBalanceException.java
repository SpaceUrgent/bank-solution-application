package spaceurgent.banking.exception;

public class AmountExceedsBalanceException extends RuntimeException {
    public AmountExceedsBalanceException(String message) {
        super(message);
    }
}
