package spaceurgent.banking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "accounts")
@Getter(value = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_id_sequence")
    private Long id;
    @Column(nullable = false)
    private BigDecimal balance;

    @Transient
    private final static int BALANCE_SCALE = 2;

    protected Account() {
    }

    public Account(BigDecimal initialBalance) {
        requireNonNull(initialBalance, "Initial balance is required");
        if (initialBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Initial balance can't be less than 0");
        }
        this.balance = initialBalance.setScale(BALANCE_SCALE, RoundingMode.FLOOR);
    }
}
