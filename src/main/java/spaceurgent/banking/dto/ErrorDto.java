package spaceurgent.banking.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

@Data
public class ErrorDto {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant timestamp;
    private final int code;
    private final String message;
    private final String path;

    public ErrorDto(Integer statusCode,
                    String message,
                    String path) {
        this.timestamp = Instant.now();
        this.code = requireNonNull(statusCode, "Status statusCode is required");
        this.message = requireNonNull(message, "Message is required");
        this.path = requireNonNull(path, "Path is required");
    }
}
