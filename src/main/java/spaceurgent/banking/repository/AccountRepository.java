package spaceurgent.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spaceurgent.banking.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
