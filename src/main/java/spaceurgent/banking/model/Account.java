package spaceurgent.banking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import spaceurgent.banking.exception.AmountExceedsBalanceException;
import spaceurgent.banking.utils.AmountUtils;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;
import static spaceurgent.banking.utils.AmountUtils.isNegativeOrZero;
import static spaceurgent.banking.utils.AmountUtils.round;

@Entity
@Table(
        name = "accounts",
        indexes = @Index(columnList = "number")
)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_number_sequence_generator")
    private Long id;
    @Column(nullable = false, unique = true)
    private String number;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency = Currency.UAH;
    @Column(nullable = false)
    private BigDecimal balance;

    protected Account() {
    }

    public Account(String accountNumber, BigDecimal initialBalance) {
        requireNonNull(accountNumber, "Account number is required");
        requireNonNull(initialBalance, "Initial balance is required");
        if (AmountUtils.isNegative(initialBalance)) {
            throw new IllegalArgumentException("Initial balance can't be less than 0");
        }
        this.number = accountNumber;
        this.balance = round(initialBalance);
    }

    public void deposit(BigDecimal amount) {
        validateTransferAmount(amount);
        this.balance = round(this.balance.add(amount));
    }

    public void withdraw(BigDecimal amount) throws AmountExceedsBalanceException {
        validateTransferAmount(amount);
        checkBalanceCoverWithdraw(amount);
        this.balance = round(this.balance.subtract(amount));
    }

    private void checkBalanceCoverWithdraw(BigDecimal amount) throws AmountExceedsBalanceException {
        if (this.balance.compareTo(amount) < 0) {
            throw new AmountExceedsBalanceException("Withdraw amount exceeds balance");
        }
    }

    private void validateTransferAmount(BigDecimal amount) {
        requireNonNull(amount, "Amount is required");
        if (isNegativeOrZero(amount)) {
            throw new IllegalArgumentException("Transfer amount must be greater than 0");
        }
    }
}
