package spaceurgent.banking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import spaceurgent.banking.exception.InvalidAmountException;

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
    private Long balance;

    protected Account() {
    }

    public Account(Long initialBalance) {
        assert initialBalance != null : "Initial balance is required";
        if (initialBalance < 0L) {
            throw new InvalidAmountException("Initial balance can't be less than 0");
        }
        this.balance = initialBalance;
    }
}
