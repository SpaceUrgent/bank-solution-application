package spaceurgent.banking.service;

import spaceurgent.banking.model.Account;

import java.math.BigDecimal;

public interface AccountService {

    Account createAccount(BigDecimal initialBalance);
}
