package spaceurgent.banking.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import spaceurgent.banking.exception.AccountNotFoundException;
import spaceurgent.banking.exception.InvalidAmountException;
import spaceurgent.banking.service.AccountService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public AccountDetailsDto createAccount(@RequestParam(name = "balance", defaultValue = "0.00") BigDecimal balance) {
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {InvalidAmountException.class})
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
}
