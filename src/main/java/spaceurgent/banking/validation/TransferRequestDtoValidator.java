package spaceurgent.banking.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spaceurgent.banking.dto.TransferRequestDto;
import spaceurgent.banking.exception.ValidationException;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class TransferRequestDtoValidator implements Validator<TransferRequestDto> {
    private final Validator<String> accountNumberValidator;

    @Override
    public void validate(TransferRequestDto transferRequestDto) {
        requireNonNull(transferRequestDto, "Transfer request dto is required");
        accountNumberValidator.validate(transferRequestDto.getSourceAccountNumber());
        accountNumberValidator.validate(transferRequestDto.getTargetAccountNumber());
        if (Objects.equals(transferRequestDto.getSourceAccountNumber(), transferRequestDto.getTargetAccountNumber())) {
            throw new ValidationException("Invalid transfer request. Source and target account numbers can't be equal");
        }
    }
}
