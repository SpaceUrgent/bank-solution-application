package spaceurgent.banking.service;

import spaceurgent.banking.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    Account createAccount(BigDecimal initialBalance);

    List<Account> findAccounts();

    Account findAccount(String accountNumber);
}
