package spaceurgent.banking.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import spaceurgent.banking.dto.AccountDetailsDto;
import spaceurgent.banking.dto.AccountsDto;
import spaceurgent.banking.dto.ErrorDto;
import spaceurgent.banking.dto.TransferRequestDto;
import spaceurgent.banking.exception.AccountNotFoundException;
import spaceurgent.banking.exception.AmountExceedsBalanceException;
import spaceurgent.banking.exception.ValidationException;
import spaceurgent.banking.service.AccountService;

import java.math.BigDecimal;

import static spaceurgent.banking.api.ApiConstants.AMOUNT_PARAMETER_NAME;
import static spaceurgent.banking.api.ApiConstants.BALANCE_DEFAULT_VALUE;
import static spaceurgent.banking.api.ApiConstants.BALANCE_PARAMETER_NAME;
import static spaceurgent.banking.api.ApiConstants.TARGET_ACCOUNT_NUMBER_PARAMETER_NAME;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public AccountDetailsDto createAccount(@RequestParam(name = BALANCE_PARAMETER_NAME, defaultValue = BALANCE_DEFAULT_VALUE)
                                           BigDecimal balance) {
        return AccountDetailsDto.from(accountService.createAccount(balance));
    }

    @GetMapping
    public AccountsDto getAccounts() {
        return AccountsDto.from(accountService.findAccounts());
    }

    @GetMapping("/{accountNumber}")
    public AccountDetailsDto getAccount(@PathVariable String accountNumber) {
        return AccountDetailsDto.from(accountService.findAccount(accountNumber));
    }

    @PostMapping("/{accountNumber}/deposit")
    public AccountDetailsDto depositToAccount(@PathVariable String accountNumber,
                                              @RequestParam(name = AMOUNT_PARAMETER_NAME) BigDecimal amount) {
        return AccountDetailsDto.from(accountService.depositToAccount(accountNumber, amount));
    }

    @PostMapping("/{accountNumber}/withdraw")
    public AccountDetailsDto withdrawFromAccount(@PathVariable String accountNumber,
                                                 @RequestParam(name = AMOUNT_PARAMETER_NAME) BigDecimal amount) throws AmountExceedsBalanceException {
        return AccountDetailsDto.from(accountService.withdrawFromAccount(accountNumber, amount));
    }

    @PostMapping("/{sourceAccountNumber}/transfer")
    public AccountDetailsDto transferToAccount(@PathVariable String sourceAccountNumber,
                                               @RequestParam(name = TARGET_ACCOUNT_NUMBER_PARAMETER_NAME) String targetAccountNumber,
                                               @RequestParam(name = AMOUNT_PARAMETER_NAME) BigDecimal amount) throws AmountExceedsBalanceException {
        final var transferRequestDto = new TransferRequestDto(sourceAccountNumber, targetAccountNumber, amount);
        return AccountDetailsDto.from(accountService.transferToAccount(transferRequestDto));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {AmountExceedsBalanceException.class, ValidationException.class})
    public ErrorDto handleBadRequestException(Exception exception,
                                              HttpServletRequest request) {
        final var requestPath = ServletUriComponentsBuilder.fromRequest(request)
                .build().getPath();
        return new ErrorDto(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), requestPath);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = AccountNotFoundException.class)
    public ErrorDto handleAccountNotFoundException(AccountNotFoundException exception,
                                                   HttpServletRequest request) {
        final var requestPath = ServletUriComponentsBuilder.fromRequest(request)
                .build().getPath();
        return new ErrorDto(HttpStatus.NOT_FOUND.value(), exception.getMessage(), requestPath);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = Exception.class)
    public ErrorDto handle(MissingServletRequestParameterException exception,
                           HttpServletRequest request) {
        final var requestPath = ServletUriComponentsBuilder.fromRequest(request)
                .build().getPath();
        return new ErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                exception.getBody().getDetail(),
                requestPath);
    }
}
