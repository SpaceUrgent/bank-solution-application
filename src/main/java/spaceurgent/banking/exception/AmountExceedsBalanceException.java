package spaceurgent.banking.exception;

public class AmountExceedsBalanceException extends Exception {
    public AmountExceedsBalanceException(String message) {
        super(message);
    }
}
