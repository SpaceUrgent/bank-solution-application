package spaceurgent.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spaceurgent.banking.model.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByNumber(String number);
}
