package spaceurgent.banking.validation;

public interface Validator<T> {
    void validate(T validationTarget);
}
