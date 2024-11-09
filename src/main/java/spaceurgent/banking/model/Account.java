package spaceurgent.banking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import spaceurgent.banking.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.util.Objects.requireNonNull;

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

    @Transient
    private final static int BALANCE_SCALE = 2;

    protected Account() {
    }

    public Account(String accountNumber, BigDecimal initialBalance) {
        requireNonNull(accountNumber, "Account number is required");
        requireNonNull(initialBalance, "Initial balance is required");
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Initial balance can't be less than 0");
        }
        this.balance = initialBalance.setScale(BALANCE_SCALE, RoundingMode.FLOOR);
    }

    public Account(BigDecimal initialBalance) {
        requireNonNull(initialBalance, "Initial balance is required");
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Initial balance can't be less than 0");
        }
        this.balance = initialBalance.setScale(BALANCE_SCALE, RoundingMode.FLOOR);
    }
}
