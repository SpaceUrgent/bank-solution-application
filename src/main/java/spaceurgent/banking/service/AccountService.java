package spaceurgent.banking.service;

import spaceurgent.banking.dto.TransferRequestDto;
import spaceurgent.banking.exception.AmountExceedsBalanceException;
import spaceurgent.banking.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    Account createAccount(BigDecimal initialBalance);

    List<Account> getAccounts();

    Account getAccount(String accountNumber);

    Account depositToAccount(String accountNumber, BigDecimal amount);

    Account withdrawFromAccount(String accountNumber, BigDecimal amount) throws AmountExceedsBalanceException;

    Account transferToAccount(TransferRequestDto transferRequest) throws AmountExceedsBalanceException;
}
